"""simple_html_wp_to_md: Simple Python script to convert downloaded Wordpress.com HTML into equivalent Markdown-structured text."""

import sys
import os
import io
import time
import datetime
import re
import glob

# for xml parsing
from bs4 import BeautifulSoup

# for converting to markdown
import html2text

# for converting front matter
import yaml

class Post:

	def __init__(self, file_name, front_matter, body_html, front_matter_compact, body_markdown):
		self.file_name = file_name
		self.front_matter = front_matter
		self.body_html = body_html
		self.front_matter_compact = front_matter_compact
		self.body_markdown = body_markdown


def gen_compact_front_matter(front_matter_content):
	fm_stream = io.StringIO(front_matter_content)
	fm = yaml.load(fm_stream, Loader=yaml.FullLoader)
	
	front_matter_part = """layout:     post
title:      %s
date:       %s
categories: %s
tags:       %s
status:     %s 
permalink:  "%s"
"""%(repr(fm['title']), fm['date'].strftime("%Y-%m-%d %H:%M:%S"), fm['categories'], fm['tags'], fm['status'], fm['permalink'])

	return front_matter_part

def re_lang_repl(match_obj):
	if match_obj.group(1) == "bash": return r"```sh" + match_obj.group(2) + "```"
	else: return r"```" + match_obj.group(1) + match_obj.group(2) + "```"

def gen_markdown(html_content):
	h = html2text.HTML2Text()
	h.unicode_snob = 1
	h.body_width = 0
	h.dash_unordered_list = True
	h.single_line_break = False

	html_part = html_content

	# converting various wordpress code tags to markdown
	html_part = re.sub(r"\[sourcecode language=\"([a-zA-Z0-9]*)\"\](.*?)\[\/sourcecode\]", r"[pre lang=\1]\2[/pre]", html_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	html_part = re.sub(r"\[code language=\"([a-zA-Z0-9]*)\"\](.*?)\[\/code\]", r"[pre lang=\1]\2[/pre]", html_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	html_part = re.sub(r"\[code lang=([a-zA-Z0-9]*)\](.*?)\[\/code\]", r"[pre lang=\1]\2[/pre]", html_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	html_part = re.sub(r"\[code lang=\"([a-zA-Z0-9]*)\"\](.*?)\[\/code\]", r"[pre lang=\1]\2[/pre]", html_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)

	# <!-- more -->
	html_part = re.sub(r"<!-- more -->", r"[more/]", html_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)

	# preserving the line break
	html_part = re.sub(r"\n", r"<br/>", html_part, re.UNICODE)
	# convert html to md
	markdown_part = h.handle(html_part)

	# replacing [pre lang=pqr]xyz[/pre] for ```xyz```
	markdown_part = re.sub(r"\[pre lang=([a-zA-Z0-9]*)\](.*?)\[\/pre\]", re_lang_repl, markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)

	# <!-- more -->
	markdown_part = re.sub(r"\[more\/\]", r"<!-- more -->", markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)

	#  removing 'blank lines', '> blank line' and 2nd '> blank line'
	markdown_part = re.sub(r"\n\s*\n", r"\n", markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	markdown_part = re.sub(r"\n>\s*\n", r"\n", markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	markdown_part = re.sub(r"\n>\s*\n", r"\n", markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)

	# inserting extra blank line before markdown tag (#, ##, ###, >, [, <!-- more -->, **) and after of '\n'
	markdown_part = re.sub(r"\n\s*#(.*)\n", r"\n\n#\1\n\n", markdown_part, flags=re.UNICODE)
	markdown_part = re.sub(r"\n!", r"\n\n!", markdown_part, flags=re.UNICODE)
	markdown_part = re.sub(r"\n\[", r"\n\n[", markdown_part, flags=re.UNICODE)
	markdown_part = re.sub(r"\n```([a-zA-Z0-9]+)(.*?)\n```", r"\n\n```\1\2\n```\n", markdown_part, flags=re.UNICODE|re.MULTILINE|re.DOTALL)
	markdown_part = re.sub(r"\n<!-- more -->\n", r"\n\n<!-- more -->\n\n", markdown_part, flags=re.UNICODE)
	##markdown_part = re.sub(r"\n\*\*(.+)\*\*\n", r"\n\n\1\n\n", markdown_part, flags=re.UNICODE)

	# removing spacing and blank lines in <ol><li>, <ul><li>
	markdown_part = re.sub(r"\n\s*\*(.+)", r"\n*\1", markdown_part, flags=re.UNICODE)
	markdown_part = re.sub(r"\n\s*(\d\.)\s(.+)", r"\n\1 \2", markdown_part, flags=re.UNICODE)

	## * abc *
	#markdown_part = re.sub(r"\n\s\s\*\s(.+)\n", r"\n\n* \1\n\n", markdown_part, flags=re.UNICODE|re.MULTILINE)

	return markdown_part


def load_and_split_posts(dir_in_html):
	posts_array = []
	list_html_files = glob.glob(dir_in_html + '*.html') 
	print("> Loading and splitting", len(list_html_files), "HTML files from", dir_in_html)
	for path_and_filename in list_html_files:
		str_content = ""
		with io.open(path_and_filename, 'r', encoding='UTF-8') as f:
			str_content = f.read()
		str_content_splitted = str_content.split('---\n')
		str_filename = os.path.basename(path_and_filename)
		print("\t", str_filename)
		if len(str_content_splitted) == 3:
			posts_array.append( Post( os.path.splitext(str_filename)[0], str_content_splitted[1], str_content_splitted[2], gen_compact_front_matter(str_content_splitted[1]), gen_markdown(str_content_splitted[2]) ) )
		else: 
			posts_array.append( Post( os.path.splitext(str_filename)[0], str_content_splitted[1], str_content_splitted[2], "no compact front matter", "no markdown" ) )
	return posts_array


def save_posts(out_dir, posts_array):
	print("> Saving", len(posts_array), "markdown files in", out_dir)
	if not os.path.exists(out_dir):
		os.makedirs(out_dir)
	for p in posts_array:
		print("\t", p.file_name + ".md")
		with io.open( out_dir + p.file_name + ".md", 'w', encoding='UTF-8') as f:
			f.write("---\n" + p.front_matter_compact + "---\n" + p.body_markdown )


def main():
	posts_array = []
	if len(sys.argv) == 1:
		print("Usage:")
		print("\t$ python simple_html_wp_to_md.py <input-html-wordpress-dir>, <optional-markdown-output-dir>\n")
		return
	elif len(sys.argv) == 2:
		dir_in_html = sys.argv[1]
		dir_out_markdown = dir_in_html
	elif len(sys.argv) == 3:
		dir_in_html = sys.argv[1]
		dir_out_markdown = sys.argv[2]
	posts_array = load_and_split_posts(dir_in_html)
	save_posts(dir_out_markdown, posts_array)


if __name__ == '__main__':
	main()