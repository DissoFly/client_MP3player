# MP3player
毕业设计——MP3播放器

本程序为本人毕业设计《基于Android网络音乐播放器的设计与实现》中的Android客户端程序，此文章简单介绍本程序的各个功能。

## 一、功能需求分析

本系统为用户主要提供账号模块、推送模块、本地模块、交友模块、搜索模块和播放模块六大模块的功能体验。打开客户端将同时出现推送模块和播放模块，其中播放以底部播放栏的形式和其他大部分模块并存，方便用户使用其他模块的同时随时操控音乐播放操作。账号模块提供自己账号的管理，推送模块提供音乐资讯和歌单资源，交友模块提供和其他用户进行交互，搜索模块可以让用户自行搜索服务器提供的资源。</br>

    结构图如图1-1所示</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/1.png)</br>
图1-1 结构图</br>

## 二、各功能实现与截图

### 1、账号模块功能实现</br>
账号模块主要包括了用户的登录注册、自动登录，以及资料修改、忘记密码、注销等功能。图2-1所示是用户的登录界面，登录过程判断用户名密码是否正确；图2-2是用户注册界面，用户注册过程会判断用户名是否存在以及注册资料是否正确等；图2-3是用户成功后左侧菜单栏的用户信息，可以进行修改操作和注销操作。</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/1.1.jpg =200)</br>
图2-1 登录界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/1.2.jpg)</br>
图2-2 注册界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/1.3.jpg)</br>
图2-3 登录成功界面</br>

### 2、推送模块功能实现
推送模块提供用户最新的音乐和音乐资讯。此模块包含资讯模块和推荐清单模块的两个子模块，其中资讯模块可以查看最新的音乐新闻资讯，界面如图2-4和图2-5所示；推荐清单模块则可以查看个性化的音乐合集推荐（界面如图2-6所示），点击可查看详细的音乐列表并可以点击播放（界面如图2-7所示）。</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/2.1.jpg)</br>
图2-4 资讯列表界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/2.2.jpg)</br>
图2-5 详细资讯界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/2.3.jpg)</br>
图2-6 推荐清单列表界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/2.4.jpg)</br>
图2-7 详细推荐清单界面</br>

### 3、本地模块功能实现
本地模块可以对本地相关的音乐进行一系列的管理和播放。此模块包含本地音乐管理、下载管理和本地歌单管理，界面如图5-8所示。本地音乐管理包括查看本地音乐（界面如图5-9所示），扫描本地音乐功能（界面如图5-10所示），添加本地音乐到歌单（界面如图5-11所示）和删除本地音乐。下载管理（界面如图5-12所示）包括查看已下载音乐和管理正在下载音乐。本地歌单管理包括创建本地清单（界面如图5-13所示）、查看详细的本地歌单和“我的最爱”歌单</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.1.jpg)</br>
图2-8 本地模块界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.2.jpg)</br>
图2-9 本地音乐界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.3.jpg)</br>
图2-10 扫描本地音乐界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.4.jpg)</br>
图2-11 收藏到歌单界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.5.jpg)</br>
图2-12 下载管理界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/3.6.jpg)</br>
图2-13 添加本地歌单界面</br>

### 4、交友模块功能实现
交友模块实现的用户与用户之间的交流和分享。此模块有动态、私信、关注和粉丝的管理。其中动态管理可以查看关注用户及自己在音乐上的操作记录，包括对音乐点赞、对评论点赞和评论内容（界面如图5-14所示），其中点击头像可进入相关用户的空间，可进行关注操作和私信操作（界面如图5-15所示）。私信管理可查看私信记录（界面如5-16所示），点击可进行私信（界面如图5-17所示）.关注和粉丝管理可分别查看相关的用户（界面如图5-18所示），同样也可点击查看相关用户空间。</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/4.1.jpg)</br>
图2-14 动态列表界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/4.2.jpg)</br>
图2-15 用户空间界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/4.3.jpg)</br>
图2-16 私信列表界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/4.4.jpg)</br>
图2-17 私信界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/4.5.jpg)</br>
图2-18 关注列表界面</br>

### 5、搜索模块功能实现
搜索模块提供用户使用关键字和设定搜索类型来进行对音乐的搜索。用户可以选择对歌曲、歌手及专辑来进行对关键字的搜索。相关界面如图5-19和5-20所示。</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/5.1.jpg)</br>
图2-19 可进行搜索类型设置</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/5.2.jpg)</br>
图2-20 使用歌手类型搜索的界面</br>

### 6、播放模块功能实现
播放模块提供用户对音乐播放的一系列操作，包括播放暂停、上下首切换、播放模式切换、播放列表查看、拖动进度条、查看歌词等操作（界面如图5-21所示）。其中查看播放列表界面如图5-22所示，查看歌词界面如图5-23所示。除此之外若播放网络音乐时可以进行下载、评论和点赞操作，其中评论界面如图5-24所示。</br>

![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/6.1.jpg)</br>
图2-21 播放模块界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/6.2.jpg)</br>
图2-22 查看歌词界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/6.3.jpg)</br>
图2-23 播放列表界面</br>
![](https://github.com/DissoFly/client_MP3player/raw/master/github_picture/6.4.jpg)</br>
图2-24 评论界面</br>