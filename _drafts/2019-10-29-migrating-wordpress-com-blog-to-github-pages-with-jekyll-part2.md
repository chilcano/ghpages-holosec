---
layout: post
title:  "Migrating WordPress.com's blog to GitHub Pages with Jekyll - Part 2"
date:   2019-10-29 10:00:00 +0100
categories: misc cms github wordpress ruby jekyll migration 
tags: misc cms github wordpress ruby jekyll migration 
permalink: "/2019/10/29/migrating-wordpress-com-blog-to-github-pages-with-jekyll-part2"
---

In this blog post (Part 2) I will explain how to manage the look&feel, theme, layouts, pagination, etc. for a previous migrated WordPress.com's blog to GitHub Pages.

![Migrating WordPress.com's blog to GitHub Pages by using Jekyll](/assets/img/2019-10-14-blog-migration-wp-github.png)




## Blog post pagination

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ bundle add jekyll-paginate
```

```sh
chilcano@inti:~/git-repos/ghpages-holosec$ nano _config.yml
```

```yaml
[...]
plugins:
  - jekyll-feed
  - jekyll-paginate
[...]
paginate: 5
paginate_path: "/blog/page:num/"
```

## xxxxxx

yyyy


## References

- [Jekyll Pagination](https://jekyllrb.com/docs/pagination/)


