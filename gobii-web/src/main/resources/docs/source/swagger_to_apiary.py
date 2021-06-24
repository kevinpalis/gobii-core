#!/usr/bin/env python

import sys
import argparse
import json
import os

try:
    from collections import OrderedDict
except ImportError:
    from ordereddict import OrderedDict

swagger_json_path = "/home/vg249/gobiiproject/gobii-web/src/main/resources/docs/generated/swagger/sampletracking/swagger.json"
doc_md_path = "/home/vg249/gobiiproject/gobii-web/src/main/resources/docs/source/sampletracking/"

parser = argparse.ArgumentParser(description="Formats Swagger JSON to YAML file compatible with Apiary")

args = parser.parse_args()

with open(swagger_json_path) as js_f:
    d = json.load(js_f, object_pairs_hook=OrderedDict)

paths = d["paths"]
tags = OrderedDict()

tags_order = ["Authentication", "Project", "Experiment", "Sample", "Dataset", "NoTag"]

paths_by_tags = {}

for path in paths:
    requests = paths[path]
    x_summary = set()
    x_tag_description = None
    for crud in requests:
        tag = None
        if requests[crud]["summary"] is not None:
            md_file = doc_md_path + "_".join(requests[crud]["summary"].split(" ")) + ".md"
            if os.path.isfile(md_file):
                with open(md_file) as md_f:
                    requests[crud]["description"] = md_f.read()
        if "tags" in requests[crud]:
            if requests[crud]["tags"][0] not in tags:
                tag = OrderedDict()
                tag["name"] = requests[crud]["tags"][0]
                tags[requests[crud]["tags"][0]] = tag
            else:
                tag = tags[requests[crud]["tags"][0]]
            if requests[crud]["tags"][0] is not None:
                if requests[crud]["tags"][0] in paths_by_tags:
                    paths_by_tags[requests[crud]["tags"][0]].add(path)
                else:
                    paths_by_tags[requests[crud]["tags"][0]] = set()
                    paths_by_tags[requests[crud]["tags"][0]].add(path)
            else:
                if "NoTag" in paths_by_tags:
                    paths_by_tags["NoTag"].add(path)
                else:
                    paths_by_tags["NoTag"] = set()
                    paths_by_tags["NoTag"].add(path)
        if "x-summary" in requests[crud]:
            x_summary.add(requests[crud]['x-summary'])
    if len(x_summary) > 0:
        requests["x-summary"] = ", ".join(x_summary)

security_definitions = d["securityDefinitions"]

for tag in tags:
    tag_description_file = doc_md_path + tag  + "_Overview.md"
    if os.path.isfile(tag_description_file):
        with open(tag_description_file) as tag_f:
            tags[tag]["description"] = tag_f.read()

d["tags"] = tags.values()

paths = OrderedDict()
unorderder_paths = {}

if len(tags_order) > 0:
    for tag in tags_order:
        if tag in paths_by_tags:
            for path in paths_by_tags[tag]:
                paths[path] = d["paths"][path]

for path in d["paths"]:
    if path not in paths:
        paths[path] = d["paths"][path]

d["paths"] = paths

with open('result.json', 'w') as json_f:
    api_doc_str = json.dumps(d)
    json_f.write(api_doc_str)

