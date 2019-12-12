---
layout: post
title:  "Migrating WordPress.com's blog to GitHub Pages with Jekyll - Part 2"
date:   2019-12-10 10:00:00 +0100
categories: misc cms 
tags: github wordpress jekyll migration python
permalink: "/2019/12/10/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part2"
---
In the first blog post [I explained how to export your WordPress.com blog and use it to generate your static blog site to be hosted in GitHub Pages](/2019/10/14/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part1 "Migrating WordPress.com's blog to GitHub Pages with Jekyll - Part 1"). Now, in this blog post (Part 2) I will explain how to manage the look&feel, theme, layouts and pagination of a previous migrated WordPress.com's blog to GitHub Pages. 
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


## Blog post Pagination and Excerpts

By default Jekyll since version 3 includes the `jekyll-paginate` plugin in the `Gemfile` and in the `_config.yml` under the plugins section.
But if you don't have it, add it with this command:

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ bundle add jekyll-paginate
```

To configure the Blog pagination and the showing excerpts we have to edit the `_config.yml` file which affect at global level.

```yaml
...
...
## Pagination for blog posts: https://jekyllrb.com/docs/pagination/
paginate: 5
paginate_path: "/blog/pg:num"

#show_excerpts: false
excerpt_separator: <!-- more -->
```

Once done, we have to create the `<GHPAGE_SITE_ROOT>/blog/index.html` which it will render the Blog Posts list. I recommend to start with the code snippet available [here - https://jekyllrb.com/docs/pagination/](https://jekyllrb.com/docs/pagination/), or if you prefer you can use my modified `<GHPAGE_SITE_ROOT>/blog/index.html` [file](/blog/index.html).

```yaml
---
layout: page
title: Blog
show_excerpts: true
#excerpt_separator: <!-- more -->
---
...
...
```

Where:

- `paginate: 5`: means 5 blog post for each page.
- `paginate_path: "/blog/pg:num"`: the path to the file with which all Blog Posts will be rendered and viewed, also it will include the pagination controls.
- `#show_excerpts: false`: inclusion of excerpts is disabled at global level, but enabled in `<GHPAGE_SITE_ROOT>/blog/index.html`.
- `excerpt_separator: <!-- more -->`: excerpt deparator is enabled at global level, but disabled in `<GHPAGE_SITE_ROOT>/blog/index.html`.


## Converting HTML post files to Markdown using Python

If you have gone through exportation and importation process you will have a Blog with some WordPress's HTML with unrecognizable tags and metadata, and missing links, that is normal and annoying too. For that, I looked out for some Ruby Gems or Python scripts to convert to Markdown, tweak the code and improve the readibility of new Markdown converted Posts.

I've used the (`Jekyll WordPress.com Importer`)[https://import.jekyllrb.com/docs/wordpressdotcom/], it extracts all posts and download attachments from the WordPress Export XML File and place them into `<GHPAGE_SITE_ROOT>/_posts/` and `<GHPAGE_SITE_ROOT>/assets/` folders, once done I'll run the prepared Python script to:

1. Convert HTML to Markdown.
2. Update all Markdown files to Jekyll's Markdown with a Front Matter's header.
3. Tweak all Markdown files to do compatible with Jekyll, it is removing no used tags, metadata, removing blank lines, adding break lines, etc.

You can download the Python script here: [simple_html_wp_to_md.py](/wp_export/simple_html_wp_to_md.py).

I had issues trying to improve the generated Markdown files, I used `BeautifulSoup` to manage XML, `html2text` to generate the Markdown and `re` massively to tweak the Markdown. If you know a best option, please, let me know it.

That's all, if you find this post helpful, please, share it.
Regards.

## References

- [Migrating WordPress.com's blog to GitHub Pages with Jekyll - Part 1](/2019/10/14/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part1)
- [Jekyll Layouts](https://jekyllrb.com/docs/layouts/)
- [Jekyll Themes](https://jekyllrb.com/docs/themes/)
- [Jekyll Pagination](https://jekyllrb.com/docs/pagination/)
- [WordPress.com Jekyll Importer](https://import.jekyllrb.com/docs/wordpressdotcom/)
