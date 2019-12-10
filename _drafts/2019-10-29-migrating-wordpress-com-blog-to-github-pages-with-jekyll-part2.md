---
layout: post
title:  "Migrating WordPress.com's blog to GitHub Pages with Jekyll - Part 2"
date:   2019-10-29 10:00:00 +0100
categories: misc cms github wordpress ruby jekyll migration 
tags: misc cms github wordpress ruby jekyll migration 
permalink: "/2019/10/29/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part2"
---
In the first blog post I explained 
In this blog post (Part 2) I will explain how to manage the look&feel, theme, layouts and pagination of a previous migrated WordPress.com's blog to GitHub Pages. 
Also I'll explain how to convert all HTML post files, obtained by using the `JekyllImport::Importers::WordpressDotCom`, to Markdown format by using a simple Python script.

![Migrating WordPress.com's blog to GitHub Pages by using Jekyll Part2](/assets/img/20191210-wp-github-jekyll-python-part2-1.png)

<!-- more -->

## Look&Feel: Themes and Layouts

When I created the Jekyll site (by running the `jekyll new <PATH>` command), Jekyll installed a site that uses a gem-based theme called [Minima](https://github.com/jekyll/minima).

Then, the best way to change the Look&Feel of your Github Page site is changing existing Theme/Layout; Jekyll supports an easy way to do it and here this is explained very well: 

* [Jekyll Layouts](https://jekyllrb.com/docs/layouts/)
* [Jekyll Themes](https://jekyllrb.com/docs/themes/)

So, in order to make changes in the Look&Feel I will override theme the Minima's Gem. First of all I'll locate where the Minima's Theme/Layout Gem is:

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ bundle show
Gems included by the bundle:
  * addressable (2.7.0)
  * bundler (2.0.2)
  * colorator (1.1.0)
  * concurrent-ruby (1.1.5)
  * em-websocket (0.5.1)
  * eventmachine (1.2.7)
  * fastercsv (1.5.5)
  * ffi (1.11.1)
  * forwardable-extended (2.6.0)
  * hpricot (0.8.6)
  * http_parser.rb (0.6.0)
  * i18n (1.7.0)
  * jekyll (4.0.0)
  * jekyll-feed (0.12.1)
  * jekyll-import (0.19.0)
  * jekyll-paginate (1.1.0)
  * jekyll-sass-converter (2.0.1)
  * jekyll-seo-tag (2.6.1)
  * jekyll-watch (2.2.1)
  * kramdown (2.1.0)
  * kramdown-parser-gfm (1.1.0)
  * liquid (4.0.3)
  * listen (3.2.0)
  * mercenary (0.3.6)
  * mini_portile2 (2.4.0)
  * minima (2.5.1)
...

chilcano@inti:~/git-repos/ghpages-holosec$ bundle show minima
/home/chilcano/git-repos/ghpages-holosec/vendor/bundle/ruby/2.5.0/gems/minima-2.5.1

chilcano@inti:~/git-repos/ghpages-holosec$ tree /home/chilcano/git-repos/ghpages-holosec/vendor/bundle/ruby/2.5.0/gems/minima-2.5.1
/home/chilcano/git-repos/ghpages-holosec/vendor/bundle/ruby/2.5.0/gems/minima-2.5.1
├── assets
│   ├── main.scss
│   └── minima-social-icons.svg
├── _includes
│   ├── disqus_comments.html
│   ├── footer.html
│   ├── google-analytics.html
│   ├── header.html
│   ├── head.html
│   ├── icon-github.html
│   ├── icon-github.svg
│   ├── icon-twitter.html
│   ├── icon-twitter.svg
│   └── social.html
├── _layouts
│   ├── default.html
│   ├── home.html
│   ├── page.html
│   └── post.html
├── LICENSE.txt
├── README.md
└── _sass
    ├── minima
    │   ├── _base.scss
    │   ├── _layout.scss
    │   └── _syntax-highlighting.scss
    └── minima.scss

5 directories, 22 files
```


With a clear understanding of the theme’s files, you can now override any theme file by creating a similarly named file in your Jekyll site directory.
For example:

1. Create the `<GHPAGE_SITE_ROOT>/_layouts/` and/or `<GHPAGE_SITE_ROOT>/assets/` folder if they don't exist.
2. Create a specific and simlarly file under `<GHPAGE_SITE_ROOT>/_layouts/`, if you want to change that specific part of Layout, or under `<GHPAGE_SITE_ROOT>/assets/`, if you want to change the Theme.
3. Once done you can add your own code in these created files. In my case I've created `home.html` and `post.html` in `<GHPAGE_SITE_ROOT>/_layouts_/` to modify the Minima's Layout and created `main.scss` in `<GHPAGE_SITE_ROOT>/assets/` to change the colors and fonts.

You can view these files here:

* [`<GHPAGE_SITE_ROOT>/assets/main.scss`](/assets/main.scss)
* [`<GHPAGE_SITE_ROOT>/_layouts/home.html`](/_layouts/home.html)
* [`<GHPAGE_SITE_ROOT>/_layouts/post.html`](/_layouts/post.html)


## Blog post pagination

By default Jekyll since version 3 includes the `jekyll-paginate` plugin in the `Gemfile` and in the `_config.yml` under the plugins section.
But if you don't have it, add it with this command:

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ bundle add jekyll-paginate
```

Now to configure the pagination we have to edit the `` file adding this:

```yaml
...

## Pagination for blog posts: https://jekyllrb.com/docs/pagination/
paginate: 5
paginate_path: "/blog/pg:num"

#show_excerpts: false
excerpt_separator: <!-- more -->
```

Where:

- `paginate: 5` means 5 blog post for each page.
- `paginate_path: "/blog/pg:num"` the path with which the messages will be viewed.
- `show_excerpts: false` is xxxxxxxxxxxx
- `excerpt_separator: <!-- more -->` xxxxxxxxxx 


## Converting HTML post files to Markdown using Python

```yaml
[...]
plugins:
  - jekyll-feed
  - jekyll-paginate
[...]
paginate: 5
paginate_path: "/blog/page:num/"
```

## References

- [Jekyll Layouts](https://jekyllrb.com/docs/layouts/)
- [Jekyll Themes](https://jekyllrb.com/docs/themes/)
- [Jekyll Pagination](https://jekyllrb.com/docs/pagination/)

