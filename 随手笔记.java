{	ifconfig	IP查看
		enX(X常见有下面3种类型) ：
		o：主板板载网卡，集成设备的设备索引号。  如果
		p：独立网卡，PCI网卡 
		s：热插拔网卡，usb之类，扩展槽的索引号
		nnn (数字)表示：MAC地址+主板信息计算得出唯一的序列
}

{	修改IP
	{	重启网卡	(配置完IP要重启)
		[root@xuegod63 Desktop]# systemctl restart network     //CENTOS7的网卡重启方法
		[root@xuegod63 Desktop]# service network restart     //CENTOS6的网卡重启方法
	}
	临时修改{
		{	临时修改网卡IP地址
			ifconfig 网卡名称 IP地址     ---直接修改网卡的IP地址，重启失效
		}	
		{	添加多个临时IP
			ifconfig 网卡名称:0 第一个IP地址 （netmask 子网掩码） ---增加一个IP
			ifconfig 网卡名称:1 第二个IP地址 （netmask 子网掩码） ---增加一个IP
			[root@xuegod63 ~]# ifconfig ens33:0 192.168.1.110 netmask 255.255.255.0 up
		}
		{	删除临时IP
			[root@xuegod63 ~]# ifconfig ens33:0 del 192.168.1.110
		}
	}
	永久修改{
		{
			{方法1：使用nmtui文本框方式修改IP
			[root@xuegod63 Desktop]# nmtui
			}
			{方法2：通过修改网卡配置文件改IP地址
				[root@xuegod63 ~]# vim /etc/sysconfig/network-scripts/ifcfg-ens33   
				TYPE=Ethernet
				BOOTPROTO=none      # 等号后面写：dhcp 表示动态获取IP地址，  
								   .# satic 表示表态IP，none表示不指定，就是静态。	
			}
		}
	}
}

{	RHEL/CENTOS 网络相关的配置文件路径为：
	[root@xuegod63 ~]# ls /etc/sysconfig/network-scripts/ifcfg-ens33	#IP地址，子网掩码等配置文件
	[root@xuegod63 ~]# ls /etc/sysconfig/network-scripts/ifcfg-lo  		#网卡回环地址
	[root@xuegod63 sysconfig]# cat /etc/resolv.conf    	#DNS配置文件
	[root@xuegod63 sysconfig]# cat /etc/hosts   		#设置主机和IP绑定信息
	[root@xuegod63 sysconfig]# cat /etc/hostname   		#设置主机名
}

{	防火墙	设置开机开不启动
	[root@xuegod63 ~]# systemctl status firewalld.service    #查看firewalld状态
	[root@xuegod63 ~]# systemctl stop firewalld       #关闭
	[root@xuegod63 ~]# systemctl start firewalld       #开启
	[root@xuegod63 ~]# systemctl disable firewalld     #开机自动关闭   		//RHLE7
	[root@xuegod63 ~]# chkconfig --list|grep network    #查看开机是否启动   //RHLE6
	[root@xuegod63 ~]# systemctl enable firewalld     #开机自动启动
}

{	临时&永久关闭Selinux
	[root@xuegod63 ~]# getenforce 
	Enforcing			//已经打开的意思
	[root@xuegod63 ~]# setenforce 0
	setenforce: SELinux is disabled
	永久关闭
	[root@xuegod63 ~]# vim /etc/selinux/config  
	改：7 SELINUX=enforcing     #前面的7，表示文档中第7行。方便你查找
	为：7 SELINUX=disabled
	[root@xuegod63 ~]# reboot
}

{	设置光盘自动挂载
	[root@xuegod63 ~]# vim  /etc/fstab  #在文档最后，添加以一下红色内容：
	/dev/cdrom 			      /mnt			  iso9660 defaults        0 0
	[root@xuegod63 ~]# mount -a
	mount: /dev/sr0 写保护，将以只读方式挂载
	[root@xuegod63 ~]# ls /mnt/   #可以查看到此目录下有内容，说明挂载成功
	CentOS_BuildTag  GPL       LiveOS    RPM-GPG-KEY-CentOS-7
}

{	本地YUM源
	yum的一切配置信息都储存在一个叫yum.repos.d的配置文件中，
	通常位于/etc/yum.repos.d目录下
	删除原有的文件
		[root@xuegod63 yum.repos.d]#rm -rf  /etc/yum.repos.d/*				//*/
		创建一个新的yum源配置文件，yum源配置文件的结尾必须是.repo
		[root@xuegod63 yum.repos.d]# vim  CentOS7.repo  #写入以下内容
			[CentOS7]   
			name=CentOS-server     
			baseurl=file:///mnt  
			enabled=1  
			gpgcheck=0
		参数说明：
			[CentOS7]        --->yum的ID，必须唯一 
			name=CentOS-server     ----->描述信息
			baseurl=file:///mnt    -------> /mnt表示的是光盘的挂载点  . file:后面有3个///
			enabled=1   ------>启用
			gpgcheck=0   ---->取消验证
		清空并生成缓存列表
			[root@localhost ~]# yum clean all				#清空yum缓存
			[root@localhost ~]# yum list						#生成缓存列表
		验证一下
			安装公钥
			[root@xuegod63 ~]# rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
			[root@xuegod63 yum.repos.d]# yum -y install httpd
}

{	查看当前是第几个shell
		[root@xuegod63 ~]# tty
		/dev/pts/0
		：比如同是打开亮两个中终端  上边显示的目录就会有所不同
	同时打开两终端，第一个终端执行：
		[root@xuegod63 ~]# echo xuegod > /dev/pts/1
		在第二个终端查看：有xuegod输出
	例3：对所有终端广播消息：系统10分钟后关机。
		[root@xuegod63 ~]# shutdown +10   #执行此命令后，在其他终端都会收到关机的消息
		[root@xuegod63 ~]# shutdown -c   	#取消关机
		[root@xuegod63 ~]# wall  "The system will be shut down in 10 minutes"   #广播，所有终端都能收到
}

{	区内外部命令
	使用type命令  ，语法 : type   要检测的命令
	[root@xuegod63 ~]# type cat
	cat 是 /usr/bin/cat
	[root@xuegod63 ~]# type pwd
	pwd 是 shell 内嵌
}

{	shell类型
	[root@xuegod63 ~]# cat /etc/shells 
	/bin/sh
	/bin/bash
	/sbin/nologin			//不允许登录
	/usr/bin/sh
	/usr/bin/bash			//常用的bash
	/usr/sbin/nologin		//不允许登录
	/bin/tcsh
	具体你使用的是哪一个，取决于你的用户配置，也就是说
	你可以看一下/etc/passwd文件的每一行的最后一个字段
	[root@xuegod63 ~]# head  -1 /etc/passwd
	root:x:0:0:root:/root:/bin/bash
}

{	ls命令
	[root@xuegod63 ~]# ls -laiS		//列表、显示隐藏、显示inode、大小排序
	d：目录文件
	l：链接文件
	b：块设备文件
	c：字符设备文件
	p：管道文件
	[root@xuegod63 ~]# !ls 			//加！ 会匹配上一个ls 命令
}

{	alias	别名的使用
	[root@xuegod63 ~]# alias vimens33='vim /etc/sysconfig/network-scripts/ifcfg-ens33' 
	[root@xuegod63 ~]# vimens33
	删除别名：
	[root@xuegod63 ~]# unalias vimens33
}

{	ls cd pwd history !$	基本命令
	!$  引用上一个命令的最后一个参数
	方法2： ctrl+r -》输入某条命令的关键字-》找出来对应的命令，按右光标键
	方法3： !数字   //执行历史命令中第N条命令
	方法4：!字符串  //搜索历史命令中最近一个以xxxx字符开头的命令，例如！vim
}

{	hwclock date 查看时间 更改时间
	查看硬件时间：
		[roo@xuegod63 etc]# hwclock 
	查看系统时间：
		[root@xuegod63 mnt]# date
		时区：
		UTC  （Universal Time Coordinated）：世界标准时间 
		GMT （Greenwich Mean Time）：格林尼治时间 
		CST  （China standard Time）：中国标准时间
	更改时间
		[root@xuegod63 ~]# date -s "2018-11-2 22:30"
	按照格式显示时间
		[root@xuegod63 ~]# date "+%F"
		2018-11-02
		%y	年份最后两位数位 (00-99)
		%Y	年份
		%m   month (01..12)
		%d	按月计的日期(例如：01)
		%M   minute (00..59)
		%H	小时(00-23)
		%S	秒(00-60)
		[root@xuegod63 ~]# date "+%Y%m%d"    
		20181102
		[root@xuegod63 ~]# date "+%Y-%m-%d %H:%M:%S"    #在年月日之前可以添加自己想要的符号
		2018-11-02 22:34:27
}

{	time 测试命令运行周期
	time 作用：一般用来测量一个命令的运行时间
	使用方法：time 在后面直接跟上命令和参数
	[root@xuegod63 ~]# time ls -l /etc/
	real	0m0.056s
	user	0m0.016s
	sys		0m0.040s
	说明：
	real：实际使用时间
	user：用户状态使用的时间
	sys：内核状态使用的时间
}

{	man --help -h	帮助命令
	[root@xuegod63 ~]# man find
	[root@xuegod63 ~]# find --help 
}

{	init shutdown reboot poweroff	开关机
	{	shutdown 
		[root@xuegod63 ~]# shutdown -h +10      #10分钟之后关机
		[root@xuegod63 ~]# shutdown -h 23:30     #指定具体的时间点进行关机
		[root@xuegod63 ~]# shutdown -h now      #立即关机
		[root@xuegod63 ~]# shutdown -r  22：22    #22:22 以后重启
	}
	{	init 0-6
		0 系统停机模式，系统默认运行级别不能设置为0，否则不能正常启动，机器关的
		1 单用户模式，root权限，用于系统维护，禁止远程登陆，就像Windows下的安全模式登录
		2 多用户模式，没有NFS和网络支持
		3 完整的多用户文本模式，有NFS和网络，登陆后进入控制台命令行模式
		4 系统未使用，保留一般不用，在一些特殊情况下可以用它来做一些事情。例如在笔记本电脑的电池用尽时，可以切换到这个模式来做一些设置
		5 图形化模式，登陆后进入图形GUI模式，X Window系
		6 重启模式，默认运行级别不能设为6，否则不能正常启动。运行init 6机器就会重启
		{	设置默认的运行界别
			centos7不再使用/etc/inittab文件进行默认的启动级别配置，而使用比sysvinit的运行级更为自由的target替代。
			第3运行级用multi-user.target替代。
			第5运行级用graphical.target替代。
			设置默认第三启动级别
			[root@xuegod63 ~]# systemctl set-default multi-user.target
			设置默认第五启动级别
			[root@xuegod63 ~]# systemctl set-default graphical.target
			[root@xuegod63 ~]#runlevel	//查看切换级别
		}
	}
}

{	cat grep head tail less more 	查看命令
	cat{
		
	}
	grep{	过滤
		-v 	取反
		^n	以n开头
		^$	以结束符开头
		^#	以#开头
		[root@xuegod63 ~]# egrep -v "^$|^#" /etc/login.defs
		[root@xuegod63 ~]# grep mk1 /etc/passwd
			mk1:x:1111:1110::/home/mk1:/sbin/nologin
	}
	head/tail{ 
		-10
		# head -10 [file_name]
	}
	less/more{
		
	}
}

{	vim 编辑器
	{	确保系统已经安装了VIM工具
		[root@panda ~]# rpm -qf `which vim`
		[root@panda ~]# rpm -qf `which vi`
	}
	{	进入编辑模式（区分大小写）
		i 插入	(光标前)
		I 行首插入
		a 插入	(光标后)
		A 行尾插入
		o 下一行插入 (另起一行)
		O 上一行插入(上一行插入)
		x 向后删除一个字符		等同于delete
		X 向前删除一个字符     	
		u 撤销一步   每按一次就撤销一次
		r 替换
		ctrl+p	补全操作（插入模式下当前文档内的）
	}
	{	光标定位
		kjhl	上下左右
		0 home	行首
		$ end	行尾
		3gg 3G	定位到第三行
		/string	查询字符换 n/N 切换查询到多个时
		/^d		查询以字母d开头
		/t$		查询以字母t结尾
	}
	{	删除、复制、粘贴、撤销
		y		复制单个字符
		yy		复制整行
		3yy		复制3行
		dd		删除
		p		粘贴
		x		删除光标所在位置
		D		从光标删除到行尾
		u		撤销
		ctrl+r	反撤销
		r		修改一个字符 
	}
	{	进入其它模式
		:		命令行模式
		ctrl+v  进入可视块模式
		v		进入可视模式
		V		进入可视行模式
		R		擦除改写，进入替换模式
		esc		退出模式
	}
	{	V模式（列）
		进入v模式 移动光标选择区域、
		编程的时候需要进行多行注释：
			1)、ctrl+v 进入列编辑模式
			2)、向下或向上移动光标，把需要注释、编辑的行的开头选中起来
			4)、然后按大写的I
			5)、再插入注释符或者你需要插入的符号,比如"#"
			6)、再按Esc,就会全部注释或添加了
		删除：再按ctrl+v 进入列编辑模式；向下或向上移动光标 ；选中注释部分,然后按d, 就会删除注释符号。
	}
	{	命令行模式
		:w 		保存 save
		:w! 	强制保存
		:q 		没有进行任何修改，退出 quit
		:q! 	修改了，不保存，强制退出
		:wq 	保存并退出 
		:wq! 	强制保存并退出
		:x 		保存退出
		:!ifconfig	调用系统命令
		!+命令
		:r /etc/hosts	读取其他文件。（把其他文件
						中的内容追加到当前文档中）
		:1,3 s/bin/xuegod    替换第1到3行中出现的第一个bin进行替换为xuegod
		:1,3 s/bin/xuegod/g  替换第1到3行中查找到所有的bin进行替换为xuegod
		:3 s/xue/aaaaa     #只把第3行中内容替换了
		:% s/do/xuegod/g  	将文本中所有的do替换成xuegod
		:% s/do/xuegod/gi	将文本中所有的do替换成xuegod, 并且忽略do的大小写
		:% s@a@b@g	   将文本中所有的a替换成b
	}
	{	自定义使用环境
		:set nu  	设置行号
		:set nonu 	取消设置行号
		:noh   		取消高亮显示
		永久设置环境
		vim /etc/vimrc  设置后会影响到系统所有的用户
		~/.vimrc   #在用户的家目录下，创建一个.vimrc。这样只影响到某一个用户
	}
	{	vim 同事打开多个文档
		vim -o /etc/passwd /etc/hosts	上下
		vim -O /etc/passwd /etc/hosts	左右
		ctrl+ww		文档之间的切换
	}
}

{	其他编辑器
	nano编辑器
	emacs编辑器
	GHOME编辑器gedit
}

{	diff	比较两个文件
	[root@xuegod63 ~]# cp /etc/passwd mima.txt
	[root@xuegod63 ~]# echo aaa >> mima.txt 
	[root@xuegod63 ~]# diff /etc/passwd mima.txt 
	40a41
	> aaa
	[root@xuegod63 ~]# vimdiff /etc/passwd mima.txt
}

{	iconv	解决ssh远程连接vim乱码
	-f, --from-code=名称 原始文本编码
	-t, --to-code=	输出编码
	-o, --output=FILE 输出文件名
	[root@xuegod63 ~]# iconv -f gb2312  -t utf8
		a此文件在windows下打开正常-到linux下vim打
		开是乱码.txt  -o aa.txt
}

{	unix2dos解决Linux脚本导到windows串行
	[root@xuegod63 ~]# rpm -ivh /mnt/Packages
				/dos2unix-6.0.3-7.el7.x86_64.rpm
	注： 在centos7上，unix2dos这个命令已经被集成
		到dos2unix-6.0.3-7.el7.x86_64.rpm包中。在
		centos6下需要安装unix2dos.xxx.rpm。
	[root@localhost test]# unix2dos b在Linux编辑的
				文档到windows下没有换行.sh
}

{	tree 	文件结构图
	[root@xuegod63 ~]# rpm -ivh /mnt/Packages/tree-1.5.3-2.el6.x86_64.rpm
	[root@xuegod63 ~]# tree /tmp/sda4/
}

{	三种用户
	超级用户 root
	普通用户 ：	系统用户 UID：1-999（centos7） 1-499（centos6）
				本地用户 1000+		500+
			UID 每个用户的标识，
	虚拟用户：不会用来登录，主要维持某个服务的正常运行
}

{	用户相关配置文件
	/etc/passwd		记录每个用户属性
	/etc/group		组相关
	/etc/shadow		密码文件
}

{	useradd/adduser	userdel passwd	用户
	{	useradd/adduser	添加用户
	-m	创建用户主目录
	-M	不创建用户主目录
	-u	UID设置
	-g 	初始组
	-G  附属组
	-s	shell
	-d	指定家目录
	[root@xuegod63 ~]# cat /etc/shells		//显示系统有几个shell
	[root@xuegod63 ~]# id mk1				//查看用户的uid、gid 组
	[root@xuegod63 ~]# cat /etc/default/useradd		//查看默认信息
	[root@xuegod63 ~]# useradd harry
	[root@xuegod63 ~]# useradd -G group,group2,grou3 harry	//指定多个附属组
	[root@xuegod63 ~]# useradd -u 1100 -g group1 -s /bin/bash harry
	[root@xuegod63 ~]# useradd -d /opt/mk1 mk1				//执行家目录
	}
	{	userdel			删除用户
		-r	同时删除用的家目录
		[root@xuegod63 ~]# useradd -r mk1
	}
	{	passwd			更改用户密码	
		--stdin 		基于标准的输入
		[root@xuegod63 ~]# vim /etc/login.defs  //密码相关配置
		{	egrep 过滤查看密码配置信息
			-v 	取反
			^n	以n开头
			^$	以结束符开头
			^#	以#开头
			[root@xuegod63 ~]# egrep -v "^$|^#" /etc/login.defs
		}
		[root@xuegod63 ~]# passwd mk1
		[root@xuegod63 ~]# echo 123456 | passwd --stdin mk1
	}
	{	usermod			更改用户信息
		-u	UID设置
		-g 	初始组（只能有一个）
		-G  附属组（可以多个）
		-s	shell
		-d	指定家目录
	} 
	{	/etc/skel/		模板文件
		[root@xuegod63 ~]# rm -rf /home/mk1/.bash*	//不小心删除
		[root@xuegod63 ~]# cp /etc/skel/.bash* /home/mk1
		[root@xuegod63 ~]# chown mk1:mk1 /home/mk1/bash*
	}
}

{	恢复root密码
	1、开机选内核时候选择第一项
	2、按e进行编辑
	3、移动光标至
		linux16 /vmlinuz-3.10…………ro……
	4、把ro更改为
		rw init=/sysroot/bin/sh
	5、按 ctrl+x 进入一个不太一样的root 根不同
	7、切根	
		chroot /sysroot
	8、直接修改密码
		passwd
	9、两次输入密码回车
	10、exit
	11、reboot
}

{	文件 文件的权限
	文件类型{
	p	命名管道文件
	d	目录文件
	l 	符号链接文件
	-	普通文件
	s 	socket套接文件 例如启用mysql，会产生mysql.sock文件
	c 	字符设备文件 	虚拟控制台 或 tty0
	b 	块设备文件	光驱啥的
	}
	rwx	UGO		基本权限{
		read write exeute 读 写 执行
		看目录有什么 	ls
		在目录里新建删除移动文件	touch mkdir rm cp
		进入目录文件	cd cat
		拥有者U	user
		所属组G	group
		其他人O other
	}
	suid sgid sticky	文件特殊权限{
		
	}
	ACL			文件拓展权限{
		
	}
}

{	chown/chgrp	 更改文件所属主 和 所属组
	-R 	递归，对目录和目录下文件进行操作
	格式	chown user:group file_name		更改主和租
			chgrp group file_name
	[root@xuegod63 ~]# chown :group1 file.txt
	[root@xuegod63 ~]# chown user1: file.txt
	[root@xuegod63 ~]# chown user1:group1 file.txt
	文件拥有者拥有其所有权限，拥有者没有写权限但可以强制修改
}

{	chmod  	更改文件的权限
	方法1 {	u+r
		(u/g/o)(+/-)(r|w|x)
		[root@xuegod63 ~]# chmod u+r a.txt
		[root@xuegod63 ~]# chmod g+rw a.txt
		[root@xuegod63 ~]# chmod o+rwx a.txt
	}
	方法2{ 二进制数字
		连续三个数字 0-7代表 rwx二进制组成转化的十进制数
		[root@xuegod63 ~]# chmod 777 a.txt
			-rwx rwx rwx
		[root@xuegod63 ~]# chmod 444 a.txt
			-r-- r-- r--
	}
	默认权限{ umask
		配置文件使其永久有效{
			vim /etc/profile
			1默认权限和umask转换为2进制
			2对umask取反
			3默认权限与umask取反的值与运算
			4得到的二进制转化为10进制
			文件默认权限 = 666 - umask值
			目录默认权限 = 777 - umask值
		}
		临时有效{
			[root@xuegod63 ~]# umask 044
		}
	}
}

{	suid sgid sticky	文件的特殊权限
	文件的特殊权限{
		suid 只能设置在二进制可执行文件上
			程序运行时的权限从执行者变成拥有者、
		sgid 二进制程序或者目录
			在sgid目录下创建文件时候，创建文件
			所属组会继承上级目录的所属组
		sticky 目录
			粘滞位 针对目录 文件无效  防删除位
			拥有者才可以删除
	}
	应用格式{
		chmod u+s/u=4
		chmod g+s/g=2
		chmod o+t/o=1
		chomd 4755
	}
	当mk1执行命令passwd时候，mk1会临时拥有root对这个文件的权限
	[mk1@xuegod63 ~]# passwd
}

{	getfacl setfacl			ACL权限（例外的权限）
	假设mk1 和文件 a.txt 没有任何关系（不同组）
	但是要设置mk对a.txt拥有某种权限		例外
	getfacl{	查看权限
		[root@xuegod63 ~]# getfacl /tmp/a.txt
	}
	setfacl{
		-m
		-R	递归 为目录时
		格式：
			setfacl -m u:mk:rwx file_name	//针对文件
			setfacl -R -m d:u:mk:rex dir_name	//针对目录
			setfacl -x u:mk dir_name
			setfacl -b dir_name	去掉所有acl权限
	}
}
	
{	chattr/lsattr		root无法删除的文件(从centos6开始)
	-i	不能被修改
	-a	只能追加
	……
	chattr i file_name		添加拓展权限
	chattr -i file_name		删除拓展权限
	lsattr file_name		查看attr权限
}


{	rpm包	安装 查看 卸载 rpm软件包
	软件包类型{
		rpm二进制包	(已经使用gcc编译后的)
		tar源码包	(需要编译)
	}
	获取方式{
		centos 	系统光盘
		rpmfind.net
		官方网站 mysql	nginx 软件
	}
	rpm 安装{
		-i install的意思。安装软件包
		-v 显示附加信息， 提供更多详细信息
		-V 校验 ，对已安装的软件进行校验
		-h --hash 安装时输出####标记
		-U 更新
		[root@xuegod63 ~]# rpm -V [包名] 	//检查命令是否被 黑客 修改
		[root@xuegod63 ~]# rpm -Vf [文件路径] //检查具体文件
		[root@xuegod63 ~]# rpm -Va			 //检查所有命令是否内修改
		[root@xuegod63 ~]# rpm -Va > rpm_Check.txt //检查所有命令是否
								//被修改 并把被修改的信息重定向到文件中
		[root@xuegod63 ~]# rpm -ivh /mnt/Packages/zsh-5.0.2-28.el7.x86_64.rpm 
		默认安装位置是 /var/lib/rpm
	}
	epel源{是系统自带的base源的扩展
		rpm -ivh http://dl.fedoraproject.org/pub/
				epel/epel-release-latest-7.noarch.rpm
	}
	rpm 查询{
		-q 常与其他参数组合使用
		-a 查询所有已安装
		-f 查询系统文件所属哪个软件包
		-i 显示已安装的rpm软件包信息后面之接跟包名
		-l 查询软件包文件安装的位置
		-p 查询未安装的软件包性关系系 ，后跟包名
		-R 查询软件的依赖性
		[root@xuegod63 ~]# rpm -q zsh 
		[root@xuegod63 ~]# rpm -qa
		[root@xuegod63 ~]# rpm -qa | grep vim
		[root@xuegod63 ~]# which find			//查看find，命令的目录
		[root@xuegod63 ~]# rpm -af /usr/bin		//
		[root@xuegod63 ~]# rpm -pqi [报的绝对路径] //查看包的详信息
		[root@xuegod63 ~]# rpm -pql [报的绝对路径] //查看没安装包安装
												   //后将生成哪些文件
	}
	rpm包卸载和升级{
		-e(erase) [包名]	卸载
		--nodeps 			忽略包的依赖包
		-Uvh					升级
		[root@xuegod63 ~]# rpm -e zsh
		[root@xuegod63 ~]# rpm -Uvh /mnt/Packages/zsh-5.0.2-28.el7.x86.rpm 
	}

	rpm包依赖解决{
		手动 ：
			根据提示挨个安装以来的包
	}
}

{	mount/umount	光盘的挂载
	[root@xuegod63 ~]# mount /dev/cdrom /mnt
	[root@xuegod63 ~]# mount /dev/sr0 /mnt
	[root@xuegod63 ~]# umount /mnt
}

{	which	查找
	[root@xuegod63 ~]# which bash 
		/usr/bin/bash
}

{	本地YUM源
	yum的一切配置信息都储存在一个叫yum.repos.d的配置文件中，
	通常位于/etc/yum.repos.d目录下
	删除原有的文件
		[root@xuegod63 yum.repos.d]#rm -rf  /etc/yum.repos.d/*				//*/
		创建一个新的yum源配置文件，yum源配置文件的结尾必须是.repo
		[root@xuegod63 yum.repos.d]# vim  CentOS7.repo  #写入以下内容
			[CentOS7]   
			name=CentOS-server     
			baseurl=file:///mnt  
			enabled=1  
			gpgcheck=0
		参数说明：
			[CentOS7]        --->yum的ID，必须本地唯一 
			name=CentOS-server     ----->描述信息
			baseurl=file:///mnt    -------> /mnt表示的是光盘的挂载点  . file:后面有3个///
			enabled=1   ------>启用
			gpgcheck=0   ---->取消验证
		清空并生成缓存列表
			[root@localhost ~]# yum clean all				#清空yum缓存
			[root@localhost ~]# yum list						#生成缓存列表
		验证一下
			[root@xuegod63 yum.repos.d]# yum -y install httpd
}

{公钥的目录
	公钥验证私钥加密
	/etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
	file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
}

{	wget 下载
	-O [存放到某个文件中]
	格式： wget -O [文件目录] [网址]
	[root@xuegod63 yum.repos.d]# wget -O /etc/yum.repos.d/
		Centos-Base.repo http://mirrors.com/repo/CentOS-7.repo
		如果下载后不能用 打开 阿里源 http://mirrors.aliyun.com/centos/
	找到centos7最新版本号	
		然后执行 sed 进行全文替换
	[root@xuegod63 ~]# sed -i 's/$releasever/7.4.1708/q' /etc/yum.repos.d/CentOS-Base.repo 
	[root@xuegod63 ~]# yum clean all	//清空一下yum源
	[root@xuegod63 ~]# yum list			//查看列表
		
}

{	$releasever  $basearch		系统版本的变量
	$releasever = 系统版本号(7.6.1810)
	root@xuegod63 ~# cat /etc/centos-release	//查询系统版本号
		CentOS Linux release 7.6.1810 (Core)
	$basearch = x86_64	//通常都是等于这个
}

yum使用{
	
	install -y [httpd]		//直接安装 
	update [kernel]	//升级 ，改变软件设置和系统设置，系统版本内盒都升级
	upgrade 		//升级 ，不改变软件设置和系统设置,内盒不会改变
	info [httpd]查看//rpm包的作用 
	provides [usr/bin/find]//查看那个安装包安装的
	remove [包名]	//卸载包
	search [keyword] //按照关键字搜索软件包
	root@xuegod63 ~]# yum install -y httpd //安装个软件包
	root@xuegod63 ~]# yum -y update   //直接整个系统升级
	
	yum报错{
		1 确定光盘是否链接，光盘是否挂载
		2 配置文件种格式是否正确，字母，字符，挂载和配置文件设置是否一致
		3 网络源需要联网，操作和RPM类似，只是会安装依赖项
	}
}

{ 	yum grouplist yum安装开发工具软件包组
	# yum grouplist //查看哪些软件包组
	# yum groupinstall -y 'Development tools	//安装软件开发包
}

{	临时切换语言环境
	# echo $LANG
	zh_CN.UTF-8
	# LANG="en.US.UTF-8"
}

{	源码编译和安装
	1 编译环境 如 gcc gcc-c++编译器 make
	2 准备软件 ：nginx-1.12.2.tar.gz
	部署Nginx
		安装依赖包
	# yum -y install gcc gcc-c++ make zlib-devel pcre-devel openssl-devel
	pcre-devel	：	提供正则表达式支持
	openssl-devel：	加密的提供 https 支持
	zlib-devel ：	提供压缩库
	tar包解压{
		# tar -zxvf nginx-1.12.2.tar.gz
	}
	源码编译三把斧{
		./config{ 	生成makefile文件//可以配置各种参数
			--prefix=[路径]		//制定安装路径（防止删除不干净
								//直接删除指定目录直接删除源码包）
		}	
		make -j 4	制定使用4核心编译
		make install	
	}
	make clean //删除 makefile he objs
}

{	源码编译安装出错解决方法 5 种解决方法
		查询关键字
		yum search 'keyword'
		……
}


{	tar	文档的归档和压缩
	1 tar
	2 zip
	3 gzip bzip xz管理压缩文件	file-sort
	tar 命令文件的归档和压缩{ 生成 a.tar 
		归档{
			-c 创建新的存档
			 -x 提取 解压文件
			 -v 现实执行详细过程
			 -f 制定备份文件的名字
			 -t 列出备份文件内容，不解包，查看包内容
			 -C +[目录]		制定解压位置
			 格式：tar [-vxvf] [file]
			# tar -cvf grub.tar /boot/grub  //压缩文件
			# tar -xvf grub.tar				//在当前目录下解压
			# tar -xvf grub.tar -C /opt/	//解压到指定文件
			# tar -cvf back.tar [file1] [dir1] [file2]
			# tar -tvf grub.tar				//查看包内的文件
		}
		压缩{
			-z 以gzip方式压缩	拓展名： tar.gz
			-j 以bz2方式压缩	拓展名： tar.bz2
			-J 以xz 方式压缩	拓展名： tar.xz
			# tar zcvf tec.tar.gz /etc
			# tar zxvf etc.tar.gz -C /opt/
			# tar jcvf tec.tar.bz2 /etc
			# tar jxvf etc.tar.bz2 -C /opt/
			# tar Jcvf tec.tar.xz /etc
			# tar Jxvf etc.tar.xz -C /opt/
			# tar xvf etc.tar.xz -C /opt/	
		}
	}
}

zip unzip管理压缩文件{
	-r 递归， 压缩目录时试用
	-d +[目录] 解压到目录
	# zip a.zip /etc/passwd
	# zip alljpg.zip *.jpg
	# zip -r grub.zip /boot/grub
	# unzip grub.zip
	# unzip grub.zip -d /opt/
}

gzip/bzip/xz/file/sort{压缩查看（常用来压缩文件，少目录）
	压缩{
		# mkdir xuegod
		# touch xuegod/a.txt
		# gzip xuegod/a.txt		//会把源文件删掉
		（bzip,xz这两个工具可以加参数 -k 来保留下源文件）
		# xz -k xuegod/a.txt
		# bzip -k xuegod/a.txt
	}
	解压{
		-d 解压 
		gzip -d 文件 
		bzip2 -d 文件 -k 
		xz 	 -d 文件 -k
		unxz 文件 -k
	}	
}


{	file 查看文件类型
		与windows不同 linux不一定是以后缀明确定文件类型的
	作用 ：确定文件类型
	# file /etc/passwd
		/etc/passwd:ASCII test
}

{	ls 按一定顺序查看文件
	-r 从打到小
	-h 文件大小更清楚的显示（转化单位）
	ls -ltr		按时间排序
	ls -lSr		按大小排序 （从小到大）
	ls -lSr		按大小排序 （从大到小）	
	ls -lS | more	管道输出给 more 命令
}

{	du 查看目录大小
	-sh
	# du -h /etc	//查看目录
	# du -sh /etc	//查看目录大小
	# du -h /etc | sort -rh | more //吧etc下所有文件安到小排序显示
}	

{	df 查看分区大小
	# df -h	快速查看磁盘分区大小
}

{	sort 排序处理大量数据
	-n 	顺序 (小到大排序)（默认）
	-r  倒叙 (从大到小)
	-M 	按照月份排序（英文单词的月份）
	
	按照字母顺序进行排序
	# cat /etc/passwd | sort | more
	# cat /file1 | sort -n
	
	-t 制定区分位置的字符
	-k1 -k2 -k3 按照分隔符分开的第几列进行排序
	-r 反序
	# sort -t ":" -k3 -n /etc/passwd //以uid 从小到大排序
		root:x:0:0: root:/bin/bash
		usr1:x:1:6: usr1:/bin/bash
		usr2:x:2:3: usr2:/bin/bash
		...
}


{ 	ps Centos7 - 系统进程管理
	进程是已启动的可执行程序实例-其组成：
		1 已分配内存的地址空间
		2 安全属，包括所有权凭据和特权
		3 程序代码的一个或多个执行线程 
		4 进程状态
	程序 ：二进制文件、静态 /bin/date,/usr/sbin/sshd
	进程 ：是程序的过程， 动态，有生命周期及运行状态 
	
	进程 PID
		每个进程分配唯一的进程ID(PID)
		父进程 的ID (PPID)
		所有进程都是 第一个进程的后代
		centos5/centos6  PID=1 的进程 是init
		centos7 PID=1 的进程 是 systemd
	父进程退出，子进程没有退出，没有父进程管理了，就变成 0僵尸进城
	
	进程ID（PID）：是唯一的数值
	进程的父进程（PPID）
	启动进程的 ID （UID）和所属组（GID）
	进程的状态 ： 运行 R	休眠 S	 僵尸 Z
}

{	ps 查看进程工具
	-a	显示跟当前终端关联的所有进程
	-u	显示某用户ID的所有进程
	-x	显示所有进程
	-ef 显示更多信息
	# ps aux | head
(显示如下11列)
USER  PID  %CPU  %MEN  VSZ  RSS  TTY  STAT  START  TIME  COMMAND
最后一列的数据有的 有方括号 []  
USER  	启用该进程的用户
PID  	进程 ID
%CPU  	占用 CPU 百分比
%MEN  	占用 内存 百分比
VSZ  	进程占用虚拟内存大小（KB）
RSS  	进程占用物理内存大小（KB）
TTY  	运行在哪个终端上 （？表示与终端无关。一般是内盒的）
			tty1-tty6 是本机上的登入者程序。
			pts/0 等等的，表示有网络连接主机的程序
STAT  	当前进程状态 	R 正在运行
						S 休眠中
						T 正在侦测活停止了
						Z 该应用已经终止，但其父进程无法正常终止他（僵尸进程）
						D 不可中断状态
			5个进本进程后加一些字母。含义如下
				<	运行在高优先级上
				N 	运行在低优先级上
				L 	进程有页面锁定在内存中
				s	进程是控制进程
				l 	进程是多线程的
				+	当前进程运行在前台
START	被触发启动时间
TIME  	进程使用 CPU 运作的时间
COMMAND	该程序的实际指令
	注：
	 ctrl+c	发送 SIGINT 信号，终止一个进程
	 ctrl+z 发送 SIGSTOP 信号， 挂起一个进程(作业放到后台)(暂停)
	 ctrl+d 不发送信号，标识特殊二进制值，表示 EOF，代表输入完成或注销
}

{	uptime/top 查看系统负载 动态管理进程 //必会
	[mk@xuegod63 ~]$ uptime
	12:36:51 up  2:49,  3 users,  load average: 0.00, 0.01, 0.05
	12:36:51	当前时间
	up  2:49	系统运行时间
	3 users		当前登录用户（包括系统本身和终端数）
	load average: 0.00, 0.01, 0.05 系统负载，即任务的平均长度。
				1分钟， 5分钟 15分钟 前到现在的平均值
				任务队列的平均长度：系统分时运行的任务数
		经验值：当服务器为 1 核心时 load average中数字 >=3  负载过高
					  如果 4 和心时 load average中数字 >=12 负载过高
}

{	top	动态插卡进程信息
	相当于任务管理器
	# top
		top - 12:50:29 up  3:03,  3 users,  load average: 0.05, 0.03, 0.05
		Tasks: 226 total,   1 running, 225 sleeping,   0 stopped,   0 zombie
		%Cpu(s):  0.3 us,  0.6 sy,  0.0 ni, 99.2 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
		KiB Mem :  1863252 total,   187228 free,   753704 used,   922320 buff/cache
		KiB Swap:  2097148 total,  2097148 free,        0 used.   843532 avail Mem 
	每行意义：
	第一行：和uptime弹出信息一样
	第二行：进程数量统计
	第三行：us用户使用的，sy内核的进程  ，ni用户改变过程优先级的寂进程
			，id空闲的，wa等待的（这个值大 建议更换性能好的磁盘），
			hi 硬中断 ，si 软中断 ，st 偷 虚拟机偷取物理机的时间
	第四行＆第五行：内存 ＆ 虚拟内存
		total 总计，free空闲，used使用，buff/cache (等价于free -k)
	
	top快捷键{
		默认top 3s 刷新一次，
		s 修改刷新时间
		空格 立即刷新
		p 加PID 查看某个进程
		P 按 CPU 排序 
		M 按内存排序
		T 按时间排序 
		数字1 显示每个内核的CPU使用率 
		u/U 制定显示的用户 
		h 帮助 
	}
	-p +[PID]	只查看某个进程
	# ps -aux | grep vim 
	mk  4535 。。。
	# top -p 4535	//查询单个进程
}

{	lsof -p [PID]	
	-p	查看某个进程都干了什么
	-i :22 查看22端口 或者黑客开启的后门端口是哪个进程在使用
}

{	pstree	工具使用
	-p
	# pstree -p | more
}

{	前后台进程切换
	前台进程：在终端中运行的命令，一但终端关闭前台进程就丢失
	后台进程：也叫守护进程，是运行在后台的一种特殊进程，不受
		终端控制，不需要终端交互；服务器多用守护进程 web httpd
	一些小命令（了解）{
		&		用在一个命令最后，可以把这个命令放到后台执行
		ctrl +z 将一个证在执行的前台命令放到后台，并暂停
		jobs 	查看当前有多少后台进程，是一个作业控制命令 
		fg		将后台的命令调至前台
		bg		将后台暂停的命令 继续执行
		# vim a.txt
		ctrl + z
		# jobs
		 [1]+ 已停止	vim a.txt
		# fg 1
	}
} 

{	 kill killall pkill	关闭进程
	kill 关闭进程 kill [PID]
	killall pkill 用于杀死制定名字的进程
	进程是通过信号方式来控制哦
	# kill -l 	//展示所有信号
// 1) SIGHUP	 2) SIGINT	 3) SIGQUIT	 4) SIGILL	 5) SIGTRAP
// 6) SIGABRT	 7) SIGBUS	 8) SIGFPE	 9) SIGKILL	10) SIGUSR1
//11) SIGSEGV	12) SIGUSR2	13) SIGPIPE	14) SIGALRM	15) SIGTERM
//16) SIGSTKFLT	17) SIGCHLD	18) SIGCONT	19) SIGSTOP	20) SIGTSTP
//21) SIGTTIN	22) SIGTTOU	23) SIGURG	24) SIGXCPU	25) SIGXFSZ
//26) SIGVTALRM	27) SIGPROF	28) SIGWINCH	29) SIGIO	30) SIGPWR
//31) SIGSYS	34) SIGRTMIN	35) SIGRTMIN+1	36) SIGRTMIN+2	37) SIGRTMIN+3
//38) SIGRTMIN+4	39) SIGRTMIN+5	40) SIGRTMIN+6	41) SIGRTMIN+7	42) SIGRTMIN+8
//43) SIGRTMIN+9	44) SIGRTMIN+10	45) SIGRTMIN+11	46) SIGRTMIN+12	47) SIGRTMIN+13
//48) SIGRTMIN+14	49) SIGRTMIN+15	50) SIGRTMAX-14	51) SIGRTMAX-13	52) SIGRTMAX-12
//53) SIGRTMAX-11	54) SIGRTMAX-10	55) SIGRTMAX-9	56) SIGRTMAX-8	57) SIGRTMAX-7
//58) SIGRTMAX-6	59) SIGRTMAX-5	60) SIGRTMAX-4	61) SIGRTMAX-3	62) SIGRTMAX-2
//63) SIGRTMAX-1	64) SIGRTMAX	
	用的最多的事信号 9) SIGKILL	//关闭进程
	# kill -9 [PID]
	# killall sshd
	# pkill sshd
}

{	nice renice 进程的优先级管理
	取值范围(-20~19) 越小优先级越高 默认 0
	nice 指定程序的运行优先级
		nice -n 5 command		优先级指定为5 
	renice 
		nice -10 [PID]			优先级改为 -10
	# nice -n 5 vim a.txt //指定 一个vim运行的优先级
	# renice -10 [PID] //指定 一个vim运行的优先级
}

{	screen	创建会话		//必会
	安装screen
		rpm -ivh /mnt/Packages/screen-.....rpm
	相当打开另一个终端
	安装完成后直接 
	使用流程{
		# screen		//进入另一个窗口
			进行操作，执行需要运行的备份命令，此时想离开一段时间，
			但还想让这个命令继续运行
		在screen会话窗口 按 ctrl + a + d
		[detached from 44074.pts-3.xuegod63]	//分离出来一个会话
		# screen -ls 	//显示当时的会话 获得id
		# screen -r [id]	//即可恢复
	}
	# screen -S text	//新建一个叫做test的会话
	# screen -ls		//列出当前所有会话
	# screen -r test	//回到test会话 
}



















