# Configuration file for the Sphinx documentation builder.

# -- Project information

project = 'Enigma Showdown'
copyright = '2023'
# author = ''

# release = '0.1'
# version = '0.1.0'

# -- General configuration

extensions = [
    'sphinx.ext.duration',
    'sphinx.ext.doctest',
    'sphinx.ext.autodoc',
    'sphinx.ext.autosummary',
    'sphinx.ext.intersphinx',
    'sphinx.ext.extlinks',
    'sphinx.ext.todo',
    'sphinx_tabs.tabs', # https://pypi.org/project/sphinx-tabs/
    'sphinx_search.extension', # https://pypi.org/project/readthedocs-sphinx-search/
]

todo_include_todos=True

intersphinx_mapping = {
    'python': ('https://docs.python.org/3/', None),
    'sphinx': ('https://www.sphinx-doc.org/en/master/', None),
}
intersphinx_disabled_domains = ['std']

templates_path = ['_templates']

# -- Options for HTML output

# html_theme = 'sphinx_rtd_theme'
html_theme = 'furo'

#html_theme_options = {
#}
html_theme_options = dict()

# -- Options for EPUB output
epub_show_urls = 'footnote'


extlinks = {
    'issue-page': ('https://github.com/EnigmaShowdown/enigmashowdown/issues%s', 'issues%s'),
    'issue': ('https://github.com/EnigmaShowdown/enigmashowdown/issues/%s', '#%s'),
    'blob': ('https://github.com/EnigmaShowdown/enigmashowdown/blob/%s', 'enigmashowdown/blob/%s'),
    'tree': ('https://github.com/EnigmaShowdown/enigmashowdown/tree/%s', 'enigmashowdown/tree/%s'),
}

html_sourcelink_suffix = ""
