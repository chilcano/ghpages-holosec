---
layout: post
title:  "Migrating WordPress.com's blog to GitHub Pages by using Jekyll - Part 1/2"
date:   2019-10-14 15:58:00 +0200
categories: cms github wordpress ruby jekyll migration 
---

# Migrating WordPress.com's blog to GitHub Pages by using Jekyll - Part 1/2

I would like to share my experience migrating my blog site hosted in WordPress.com to GitHub Pages in 2 parts.
In this Part 1 I will explain how to use Jekyll to export/import, how to configure GitHub Page site to host a fully blog as a headless Content Management System (CMS) based on Ruby. 
In the Part 2 I will explain how to manage the look&feel, layouts, etc.

## Create a GitHub Pages repository

I created an empty GitHub repository, in my case is `[ghpages-holosec](https://github.com/chilcano/ghpages-holosec)`, to host my migrated WordPress.com's blog, then I followed the [https://pages.github.com](https://pages.github.com) guide and configured it as `Project site`, no as `User or organization site` since my GitHub Account will host multiple Sites with different custom Domain Names. 

Once completed, you should configure your DNS records in your DNS Provider, in my case it is Gandi.Net and used the above [Matt Bailey's good guide](https://gist.github.com/matt-bailey/bbbc181d5234c618e4dfe0642ad80297). You have to consider that making these changes and their propagation across DNS and GitHub will take around 24 hours.

How to set up DNS records on gandi.net to use a custom domain on Github Pages
<script src="https://gist.github.com/matt-bailey/bbbc181d5234c618e4dfe0642ad80297.js"></script>


## Install Ruby on Ubuntu

This step must be executed once in the computer.
First of all, install Ruby.
```sh
$ sudo apt-get install ruby-full build-essential zlib1g-dev
```
Now, configure Ruby for my Linux's user.
```sh
$ echo '# Install Ruby Gems to ~/gems' >> ~/.bashrc
$ echo 'export GEM_HOME="$HOME/gems"' >> ~/.bashrc
$ echo 'export PATH="$HOME/gems/bin:$PATH"' >> ~/.bashrc
$ source ~/.bashrc

$ gem install bundler
```

## Install Jekyll with Bundler

This step and next ones must be executed per website or ruby project. In this point your configured custom DNS should be working.
I will download my empty created GitHub Page repository into `~/git-repos/ghpages-holosec/` directory.
```sh
$ git clone https://github.com/chilcano/ghpages-holosec ~/git-repos/ghpages-holosec/
```

But if you haven't created any GitHub Page repository, you can create it in your computer and after push it to GitHub.
```sh
$ mkdir ~/git-repos/ghpages-holosec/
```

```sh
$ cd ~/git-repos/ghpages-holosec/
$ bundle init
$ bundle install --path vendor/bundle
$ bundle add jekyll	// this installs jekyll and update Gemfile
```

## Create a Jekyll scaffold

Once Jekyll is installed, we can use it to create the scaffolding for our site. We need the `--force` parameter because our folder isn't empty, there are some Bundler files in it.

```sh
$ bundle exec jekyll new --force --skip-bundle .
$ bundle install
```

## Serving the site for first time

I updated the generated `_config.yml` file with information about `baseurl`, `title`, `email`, etc.
Yes, I'll tweak the `_config.yml` file to apply new design later.
Now, you will see only `Welcome to Jekyll!` sample post.

```sh
$ bundle exec jekyll serve
```

## Importing a WordPress.com's blog into GitHub Pages site

Go to https://YOUR-USER-NAME.wordpress.com/wp-admin/export.php and export your blog's content into a XML, place the xml file in `~/git-repos/ghpages-holosec/wp_export/`, once completed, execute `jekyll-import` to convert WordPress' XML format into GitHub's MarkDown format.

Install jekyll-import and its dependencies.
```sh
$ bundle add jekyll-import hpricot open_uri_redirections
```

Convert wordpress' blog to Jekyll format.
```sh
$ bundle exec ruby -r rubygems -e 'require "jekyll-import";
    JekyllImport::Importers::WordpressDotCom.run({
      "source" => "wp_export/holisticsecurity.WordPress.2019-10-15.xml",
      "no_fetch_images" => false,
      "assets_folder" => "assets"
    })'
```

## Serving the site again

```sh
$ bundle exec jekyll serve
```
You will see that all posts were imported.

## References

https://jekyllrb.com/docs/installation/ubuntu/
https://jekyllrb.com/tutorials/using-jekyll-with-bundler/
https://import.jekyllrb.com/docs/installation/
https://import.jekyllrb.com/docs/wordpressdotcom/

