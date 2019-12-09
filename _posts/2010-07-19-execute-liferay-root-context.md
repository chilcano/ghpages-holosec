---
layout:     post
title:      'Execute Liferay Portal on non ROOT context'
date:       2010-07-19 15:24:34
categories: ['Portal']
tags:       ['Liferay Portal']
status:     publish 
permalink:  "/2010/07/19/execute-liferay-root-context/"
---
Sometime we need to execute Liferay Portal in a different web context, for example in `http://www.intix.info/myportal` instead of `http://www.intix.info/`.
Then, in this scenario we have to follow next steps:  

<!-- more -->
1. Rename //webapps/ROOT to //webapps/myportal
2. Edit or create `//webapps/myportal/WEB-INF/classes/portal-ext.properties`:
> ...  
> portal.ctx=/myportal  
> ...  
1.   
Rename `//conf/Catalina/localhost/ROOT.xml` to `//conf/Catalina/localhost/myportal.xml`
2.   
Restart Liferay and go to `http://www.intix.info/myportal`, should be possible to browser around liferay without errors, if you found broken links you must change absolut URL/Path adding to begin `/myportal`, for example: update `http://www.intix.info/image/image_gallery?xyzx` with `http://www.intix.info/myportal/image/image_gallery?xyz`.
Bye.
 _Links:_  
\- Run liferay on non root context:  
http://www.liferay.com/es/community/forums/-/message_boards/message/1041350
