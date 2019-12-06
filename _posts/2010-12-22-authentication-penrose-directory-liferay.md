---
layout: post
title: Liferay Portal LDAP Authentication with Penrose Server
date: 2010-12-22 00:49:38.000000000 +01:00
type: post
parent_id: '0'
published: true
password: ''
status: publish
categories:
- Linux
- Portal
- Security
tags:
- Authentication
- LDAP
- Penrose
- Virtual Directory
meta:
  _wpas_done_twitter: '1'
  _edit_last: '578869'
  tagazine-media: a:7:{s:7:"primary";s:101:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/6_vds_browsing_new_ldap_tree.png";s:6:"images";a:16:{s:93:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/0_vds_penrose_server.png";a:6:{s:8:"file_url";s:93:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/0_vds_penrose_server.png";s:5:"width";s:3:"251";s:6:"height";s:3:"169";s:4:"type";s:5:"image";s:4:"area";s:5:"42419";s:9:"file_path";s:0:"";}s:87:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/1_vds_add_conn.png";a:6:{s:8:"file_url";s:87:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/1_vds_add_conn.png";s:5:"width";s:3:"434";s:6:"height";s:3:"395";s:4:"type";s:5:"image";s:4:"area";s:6:"171430";s:9:"file_path";s:0:"";}s:92:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/2_vds_add_conn_auth.png";a:6:{s:8:"file_url";s:92:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/2_vds_add_conn_auth.png";s:5:"width";s:3:"436";s:6:"height";s:3:"388";s:4:"type";s:5:"image";s:4:"area";s:6:"169168";s:9:"file_path";s:0:"";}s:96:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/3_vds_explore_ldap_tree.png";a:6:{s:8:"file_url";s:96:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/3_vds_explore_ldap_tree.png";s:5:"width";s:3:"716";s:6:"height";s:3:"459";s:4:"type";s:5:"image";s:4:"area";s:6:"328644";s:9:"file_path";s:0:"";}s:97:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/5_vds_employees_db_model.png";a:6:{s:8:"file_url";s:97:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/5_vds_employees_db_model.png";s:5:"width";s:3:"495";s:6:"height";s:3:"546";s:4:"type";s:5:"image";s:4:"area";s:6:"270270";s:9:"file_path";s:0:"";}s:101:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/6_vds_browsing_new_ldap_tree.png";a:6:{s:8:"file_url";s:101:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/6_vds_browsing_new_ldap_tree.png";s:5:"width";s:3:"736";s:6:"height";s:3:"605";s:4:"type";s:5:"image";s:4:"area";s:6:"445280";s:9:"file_path";s:0:"";}s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_0.png";a:6:{s:8:"file_url";s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_0.png";s:5:"width";s:3:"387";s:6:"height";s:3:"352";s:4:"type";s:5:"image";s:4:"area";s:6:"136224";s:9:"file_path";s:0:"";}s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_1.png";a:6:{s:8:"file_url";s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_1.png";s:5:"width";s:3:"610";s:6:"height";s:3:"517";s:4:"type";s:5:"image";s:4:"area";s:6:"315370";s:9:"file_path";s:0:"";}s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_2.png";a:6:{s:8:"file_url";s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_2.png";s:5:"width";s:3:"381";s:6:"height";s:3:"654";s:4:"type";s:5:"image";s:4:"area";s:6:"249174";s:9:"file_path";s:0:"";}s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_3.png";a:6:{s:8:"file_url";s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_3.png";s:5:"width";s:3:"386";s:6:"height";s:3:"661";s:4:"type";s:5:"image";s:4:"area";s:6:"255146";s:9:"file_path";s:0:"";}s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_4.png";a:6:{s:8:"file_url";s:99:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/7_vds_liferay_ldap_authn_4.png";s:5:"width";s:3:"379";s:6:"height";s:3:"588";s:4:"type";s:5:"image";s:4:"area";s:6:"222852";s:9:"file_path";s:0:"";}s:92:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/8_vds_liferay_login.png";a:6:{s:8:"file_url";s:92:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/8_vds_liferay_login.png";s:5:"width";s:3:"445";s:6:"height";s:3:"532";s:4:"type";s:5:"image";s:4:"area";s:6:"236740";s:9:"file_path";s:0:"";}s:98:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/9_vds_liferay_welcomepage.png";a:6:{s:8:"file_url";s:98:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/9_vds_liferay_welcomepage.png";s:5:"width";s:3:"665";s:6:"height";s:3:"334";s:4:"type";s:5:"image";s:4:"area";s:6:"222110";s:9:"file_path";s:0:"";}s:97:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/10_vds_liferay_myaccount.png";a:6:{s:8:"file_url";s:97:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/10_vds_liferay_myaccount.png";s:5:"width";s:3:"603";s:6:"height";s:3:"556";s:4:"type";s:5:"image";s:4:"area";s:6:"335268";s:9:"file_path";s:0:"";}s:112:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory1.png";a:6:{s:8:"file_url";s:112:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory1.png";s:5:"width";s:3:"772";s:6:"height";s:3:"416";s:4:"type";s:5:"image";s:4:"area";s:6:"321152";s:9:"file_path";s:0:"";}s:112:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory2.png";a:6:{s:8:"file_url";s:112:"http://dl.dropbox.com/u/2961879/blog20101203_virtualdirectory_portal/11_vds_verify_account_apache_directory2.png";s:5:"width";s:3:"617";s:6:"height";s:3:"481";s:4:"type";s:5:"image";s:4:"area";s:6:"296777";s:9:"file_path";s:0:"";}}s:6:"videos";a:0:{}s:11:"image_count";s:2:"16";s:6:"author";s:6:"578869";s:7:"blog_id";s:7:"2005905";s:9:"mod_stamp";s:19:"2010-12-21
    23:49:38";}
  _oembed_e26150649e94e5afde6b8b5d37e1b142: "{{unknown}}"
  _oembed_719b17158176b35491b472656c4c7d80: "{{unknown}}"
  _oembed_d391f97c23cf9518427f7bb50ff95598: "{{unknown}}"
  _oembed_f19c0b1765f2d87fdfe5167d1ecd1d6a: "{{unknown}}"
  _oembed_872ce7f4c37a6bd558c6426fe800167b: "{{unknown}}"
  _oembed_3fa53bc3df7e1e170cbddfe065c36a86: "{{unknown}}"
  _oembed_64f1948e9267a3e67920b42689986e1d: "{{unknown}}"
  _oembed_d40a1c2911841a4a2a47eac354d9a2dc: "{{unknown}}"
author:
  login: rcarhuatocto
  email: roger@intix.info
  display_name: Roger CARHUATOCTO
  first_name: ''
  last_name: ''
permalink: "/2010/12/22/authentication-penrose-directory-liferay/"
---
no markdown