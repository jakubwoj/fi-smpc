
# Config
DOCNAME = paper


# Targets
all: $(DOCNAME).pdf

pdf: $(DOCNAME).pdf clean-bbl
	latexmk -silent -c

clean: clean-bbl
	latexmk -silent -C

clean-bbl:
	rm -f *.bbl

.PHONY: all pdf clean clean-bbl


# Rules
%.pdf: *.tex *.bib
	latexmk -pdf $(DOCNAME)

