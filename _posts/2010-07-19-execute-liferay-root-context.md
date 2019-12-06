---
layout: post
title: Execute Liferay Portal on non ROOT context
date: 2010-07-19 17:24:34.000000000 +02:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Portal
tags:
- Liferay Portal
meta:
  _edit_last: '578869'
  _wpas_done_twitter: '1'
  _oembed_a06eb6873642936c58580050a263cbcd: "{{unknown}}"
  _oembed_5021165d862e95339052423a676293c7: "{{unknown}}"
  _oembed_d4d5d0c49a5230e58b51346003ae0fe7: "{{unknown}}"
  _oembed_28253835a3296c3355546d64a878e540: "{{unknown}}"
  _oembed_be973dac5e2cbf562467514fcd6b5b25: "{{unknown}}"
  geo_public: '0'
  _wpas_skip_5053092: '1'
  _wpcom_is_markdown: '1'
  _oembed_9f7c26f1ff5e2b8ee6b354edc398a682: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/07/19/execute-liferay-root-context/"
---
Sometime we need to execute Liferay Portal in a different web context, for example in `http://www.intix.info/myportal` instead of `http://www.intix.info/`.

  


Then, in this scenario we have to follow next steps:  
  


  


  

  1. Rename //webapps/ROOT to //webapps/myportal
  

  2. Edit or create `//webapps/myportal/WEB-INF/classes/portal-ext.properties`:
  

  


>   
> ...  
>   
> portal.ctx=/myportal  
>   
> ...  
> 

  


  

  1.   


Rename `//conf/Catalina/localhost/ROOT.xml` to `//conf/Catalina/localhost/myportal.xml`

  

  

  2.   


Restart Liferay and go to `http://www.intix.info/myportal`, should be possible to browser around liferay without errors, if you found broken links you must change absolut URL/Path adding to begin `/myportal`, for example: update `http://www.intix.info/image/image_gallery?xyzx` with `http://www.intix.info/myportal/image/image_gallery?xyz`.

  

  

  


Bye.

  


 _Links:_  
  
\- Run liferay on non root context:  
  
http://www.liferay.com/es/community/forums/-/message_boards/message/1041350

  

