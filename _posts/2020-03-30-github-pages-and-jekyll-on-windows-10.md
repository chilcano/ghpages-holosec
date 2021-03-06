---
layout:     post
title:      "GitHub Pages and Jekyll on Windows 10"
categories: ['misc', 'cms'] 
tags:       ['github', 'jekyll', 'ruby']
date:       2020-03-30 22:00:00 +0100
permalink:  "/2020/03/30/github-pages-and-jekyll-on-windows-10"
comments:   true
---
I have a[ Blog hosted on Github Pages created with Jekyll from Linux](https://holisticsecurity.io/2019/10/14/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part1). That [works perfectly and can publish posts frequently](https://holisticsecurity.io/2019/12/10/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part2), but now I would like to do the same but from Windows 10 laptop (older Surface 3 Pro, 4GB RAM, 64GB SSD). The aim of this post is explain you how to prepare and configure Windows 10 to publish post in a new or existing static site created with Jekyll.

To do that I'm going to follow the [Jekyll on Windows](https://jekyllrb.com/docs/installation/windows) guide, basically I'll [download, install and configure a Ruby+Devkit](https://rubyinstaller.org/downloads) version in Windows 10.

[![](/assets/blog20200330/20200330-github-pages-and-jekyll-on-windows-10-1.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200330/20200330-github-pages-and-jekyll-on-windows-10-1.png)

<!-- more -->

### Steps

Once downloaded and installed Ruby+Devkit, open a CMD as standard user and check the installed version:

```
C:\Users\rmce> ruby --version
ruby 2.6.5p114 (2019-10-01 revision 67812) [x64-mingw32]
```
 
Download existing the Github Pages site:

```
C:\Users\rmce> y:
Y:\> cd __gitrepos
Y:\__gitrepos> git clone https://github.com/chilcano/ghpages-holosec
Y:\__gitrepos> cd ghpages-holosec
```

Install Bundler:

```
Y:\__gitrepos\ghpages-holosec> gem install bundler
Y:\__gitrepos\ghpages-holosec> bundle config set path 'vendor/bundle'
```

Install Jekyll and all Gems via Bundler:

```
Y:\__gitrepos\ghpages-holosec> bundle 
```

Check if Jekyll was installed properly:

```
Y:\__gitrepos\ghpages-holosec> bundle exec jekyll -v
jekyll 4.0.0
```

Now we are ready to serve the site in our computer:

```
Y:\__gitrepos\ghpages-holosec> bundle exec jekyll serve
```

If you are using Google Analytics plugin configured, you can try this:

```
Y:\__gitrepos\ghpages-holosec> set JEKYLL_ENV=production 
Y:\__gitrepos\ghpages-holosec> bundle exec jekyll serve --incremental --watch 
Configuration file: Y:/__gitrepos/ghpages-holosec/_config.yml
            Source: Y:/__gitrepos/ghpages-holosec
       Destination: Y:/__gitrepos/ghpages-holosec/_site
 Incremental build: enabled
      Generating...
       Jekyll Feed: Generating feed for posts
                    done in 192.797 seconds.
 Auto-regeneration: enabled for 'Y:/__gitrepos/ghpages-holosec'
    Server address: http://127.0.0.1:4000/
  Server running... press ctrl-c to stop.
```

Or instead of CMD using PowerShell Terminal.
```PowerShell
Y:\__gitrepos\ghpages-holosec>
$> Set-Variable -name JEKYLL_ENV -value production; bundle exec jekyll serve --incremental --watch
```

And if you have posts in draft, run this. Only be aware to place your posts in `<site>\_drafts\` folder without `date` and `permalink` in the front-matter.
```PowerShell
$> Set-Variable -name JEKYLL_ENV -value production; bundle exec jekyll serve --watch --drafts
```

[![](/assets/blog20200330/20200330-github-pages-and-jekyll-on-windows-10-2.png){:width="400"}{:style="display:block;margin:auto;"}](/assets/blog20200330/20200330-github-pages-and-jekyll-on-windows-10-2.png)