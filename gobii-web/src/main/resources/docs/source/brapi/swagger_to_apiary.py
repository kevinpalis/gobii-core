#!/usr/bin/env python
"""
Simple python script to convert swagger json file to api blueprint file.
"""

import argparse
import json
import os
import sys


try:
    from collections import OrderedDict
except ImportError:
    from ordereddict import OrderedDict

tags_order = ["ServerInfo", "Authentication", "Studies", "Genome Maps",
              "Samples", "CallSets", "Variants", "VariantSets",
              "Genotype Calls", "NoTag"]

swagger_json_path = None
doc_md_path = None

parser = argparse.ArgumentParser(
    description="Formats Swagger JSON to YAML file compatible with Apiary")

parser.add_argument("--input",
                    help=("json file with input properties like swagger json path."))

args = parser.parse_args()


if args.input is None or not os.path.isfile(args.input):
    """
    Condition to check whether input properties file in provided.
    """
    print("Requires input file")
    sys.exit()

with open(args.input) as input_properties_file:
    input_properties = json.load(input_properties_file)
    if "swagger_json_path" in input_properties:
        swagger_json_path = input_properties["swagger_json_path"]
        with open(swagger_json_path) as js_f:
            swagger_obj = json.load(js_f, object_pairs_hook=OrderedDict)
    else:
        print("Requires swagger json file")
        sys.exit()
    if "doc_md_path" in input_properties:
        doc_md_path = input_properties["doc_md_path"]

paths = swagger_obj["paths"]
tags = OrderedDict()
paths_by_tags = {}

for path in paths:
    requests = paths[path]
    x_summary = set()
    x_tag_description = None
    for crud in requests:
        tag = None
        if ("summary" in requests[crud] and
                requests[crud]["summary"] is not None):
            if doc_md_path is not None:
                md_file = (
                    doc_md_path +
                    "_".join(requests[crud]["summary"].split(" ")) + ".md")
                if os.path.isfile(md_file):
                    requests[crud]["summary"] = requests[crud]["description"]
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

security_definitions = swagger_obj["securityDefinitions"]

if doc_md_path is not None:
    for tag in tags:
        tag_description_file = (
            doc_md_path + "_".join(tag.split(" ")) + "_Overview.md")
        if os.path.isfile(tag_description_file):
            with open(tag_description_file) as tag_f:
                tags[tag]["description"] = tag_f.read()

swagger_obj["tags"] = tags.values()

paths = OrderedDict()
unorderder_paths = {}

if len(tags_order) > 0:
    for tag in tags_order:
        if tag in paths_by_tags:
            for path in paths_by_tags[tag]:
                paths[path] = swagger_obj["paths"][path]

for path in swagger_obj["paths"]:
    if path not in paths:
        paths[path] = swagger_obj["paths"][path]

swagger_obj["paths"] = paths

with open('result.json', 'w') as json_f:
    api_doc_str = json.dumps(swagger_obj)
    json_f.write(api_doc_str)
