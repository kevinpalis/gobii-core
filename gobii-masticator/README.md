# Masticator

The Masticator consumes a data file consisting of vary-dimensional aspects and produces a stream of 1-dimensional rows. The aspects are defined via a declarative syntax. The dimensionality of each aspect are aligned to match that of the 2-dimensional output table. Further transformations and formatting can be defined via transformation aspects.

## Usage

`masticator -a {File|-} -d File -o Directory`

The Masticator takes an aspect, provided either by standard in or a file path, a data file path, and an output directory path.

## Aspects

An aspect defines how the Masticator will read a file. Thusly, we use aspect to describe how a file is read, how a table is read, and how an element is read. Furthermore, aspects may define generators, or transformations on other aspects. The nuances of the term aspect will become more apparent throughout our reading, but for now just recognize that the term aspect is a bit overloaded.

### Overall Structure

A full aspect is considered to read a file; each file is considered to have information for multiple tables; each table is considered to have multiple columns. As is such, the overall structure of an aspect is in the hierarchy of `File -> Table -> Column`. Currently, a single file is available per aspect.

```json
{"aspects":
  {"foo": {"col": ...,
           "attribute": ...}},
  {"bar": {"another": ...,
           "something": ...}}}
```

In the former example, we are loading to two tables (`foo` and `bar`). The table `foo` has aspects defined for columns `col` and `attribute`. The definition for `bar` follows a similar pattern.


### Alignment

Aspects read a stream of data, some infinite, some finite. Two aspects are considered aligned if they both have the same length, or one is infinite. Practically, this means we can read each aspect fully, and have them terminate at the same time (infinite aspects can terminate at any time).

### Generator Aspects

Generator aspects produce data streams independent of information from the file, infinitely.

#### Constant Aspect

A constant aspect continually returns the same value.

```
["CONSTANT", "0"]  =>  0, 0, 0, 0, ...
```

#### Range Aspect

A range aspect returns the numeric values starting at a given number, and incrementing upwards on each read.

```
["RANGE", "1"]  =>  1, 2, 3, 4, ...
```

### File Aspects

File aspects are those that describe content of a file, be it 0-dimensional (cells), 1-dimensional (rows and columns) or 2-dimensional (matrices). Each is parameterized with a row and a column (zero based), corresponding to the cell , the beginning of the row/column, or the top left of the matrix. For the case of 2-dimensional and higher, the right (newline) and bottom (EOF) of the file are considered to be the end of the structure.

Consider the following matrix for the latter example:

```
 +-------------------+
 |  a |  b |  c |  d |
 +-------------------+
 |  0 |  1 |  2 |  3 |
 +-------------------+
 |  4 |  5 |  6 |  7 |
 +-------------------+
 |  8 |  9 | 10 | 11 |
 +-------------------+
 | 12 | 13 | 14 | 15 |
 +-------------------+
```

```
["CELL", {"row": 0, "col": 0}] => a, a, a, a, a, ...

["COLUMN", {"row": 1, "col": 1}] => 1, 5, 9, 13

["ROW", {"row": 1}, "col": 1} => 1, 2, 3

["MATRIX", {"row": 3}, "col": 2}] => 10, 11, 14, 15
```

Let's take note of a few things here:

The cell aspect is infinitely long, much like a generator aspect. Given this, a cell aspect is automatically aligned with any other aspect. The column and row aspects generate different length streams, and are not aligned. The matrix aspect reads the all values with row/column pairs greater than it's parameters, from left to right.

### Higer Order Aspects

Now that we have an arsenal of aspects to create our information streams, we now may have needs to join or alter streams of data.

#### Json Aspect

Json aspects take an aspect with the same form as a table aspect, and join them together in the json format. Going off our previous example:

```
["JSON", {"foo": ["CELL", {"row": 0, "col": 0}],
          "bar": ["ROW", {"row": 1, "col": 1}]}]

=> {"foo": "a", "bar": "1"},
   {"foo": "a", "bar": "2"},
   {"foo": "a", "bar": "3"}
```

*Currently, only stringly typed values are supported.*

#### Transform Aspect

Transformation aspects take data streams, and push them through functions defined in the aspect file, or predefined in the system and referenced by names. Multiple aspects can be joined this way; if a transform aspect is provided multiple aspect arguments, each stream will be provided to the function.

Currently, inline transforms are supported for the `clojure` (written as "clj" or "clojure" under the `lang` field), or `sed` (as `sed`). Named transformations can be written in Java using the `@Transformation` annotation.

Again, from our previous example:

```
["TRANSFORM",
  {"lang": "clj", "script": "(fn [a b] (str a "/" b))"},
  ["COLUMN", {"row": 1, "col": 1}],
  ["MATRIX", {"row": 3}, "col": 2}]]

=> 1/10, 5/11, 9/14, 13/15

["TRANSFORM", "IUPAC->2LETTER", ["CELL", {"row": 0, "col": 0}]] => aa, aa, aa, ...
```

#### Align Aspect

Align aspects are designed to unify 2-dimensional and 1-dimensional aspects. In our previous example, the matrix aspect and the column aspect were incidentally aligned (they had the same number of attributes). Often, matrices will be have the same number of rows as a column or row has cells.

The align aspect repeats a column cell for each cell in a row of a matrix aspect. The same is true for the case of row. Since this is hard to word out, let's look at an example.

Consider the following data:

```
|  bias |  x |  y |  z |
|-------+----+----+----|
|    -1 |  1 |  2 |  3 |
|     0 |  2 |  4 |  6 |
|     1 |  3 |  6 |  9 |
```

We have 3 columns `x,y,z` which define some form of data. Additionally we have a `bias` column. Consider that when parsing this we want each value stored in `x,y,z` to be summed with the corresponding `bias`. This is exactly what align aspects are for.

```
["ALIGN", ["COLUMN", {"row": 1, "col": 0}],
          ["MATRIX", {"row": 1, "col": 1}]]
=> [-1 1], [-1 2], [-1 3]
   [ 0 2], [ 0 4], [ 0 6]
   [ 1 3], [ 1 6], [ 1 9]
```

If we wrap this in a transform aspect, with a 2-artiy function,  we can then apply the addition:

```
["TRANSFORM",
  {"lang": "clj",
   "script": "(fn [a b] (+ a b))"}
  ["ALIGN", ["COLUMN", {"row": 1, "col": 0}],
            ["MATRIX", {"row": 1, "col": 1}]]
  => 0, 1, 2
     2, 4, 6
     4, 7, 10
```
