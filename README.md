## Java CSV Reader

This is an exercise to read a file in CSV (comma separated values) format using Java Stream Pipeline.

### Instructions

Overview: This is an exercise to explore your competency in the following areas:

- modern java language features like Streams, Predicates, Function objects etc.
- algorithmic thinking and modular design in terms of classes and methods.
- general awareness of constraints when processing large files of data.

Requirements:

- Write a java program in the form of a java Stream pipeline to read a file in CSV (comma separated values) format (example attached). <br>
  The first line of the file contains the names of the fields.<br>
  The other lines contain values corresponding to the names in the first line.
<br><br>
- The program should read each of the other lines into a Map object and print its contents. Please ensure all lines are read even when some lines have errors.
<br><br>
- Every entry in the Map should contain:<br>
  Key - a string containing the field name (from the first row) <br>
  Value - the corresponding value (from the current row)
<br><br>
- A Value could be a String (sequence of characters), Double (floating point number) or Integer (no decimal point) and should be read as an Object of the appropriate type.
- Null values are possible as are empty strings.
- String values may be quoted or unquoted and may contain commas.
- Please ignore leading and trailing spaces for numerical values and unquoted string values.
- Leading and trailing spaces for quoted string values are significant and must not be ignored.
<br><br>

Acceptance criteria:

- Must read the lines of the file as a Stream. The input file may have millions of lines of data.
<br><br>
- Use of explicit loops like while, for etc. are not permitted. Please use Stream functions like foreach, filter, map, join etc. to iterate and transform.<br>
  This will be used to evaluate the competency in the modern java language features.
<br><br>
- There are many ways to parse data in java. Please choose an appropriate mechanism and describe why you chose it.
<br><br>
- If you want to make improvements or extend your code but do not have time, leave a //TODO comment in your code along with a description of what you would have done given time, and why.

Bonus points:

- Provide a solution that makes use of parallelism in processing the rows.

Please limit the time you work on your solution to four hours.<br>
Please commit the solution to a public git repository of your choice (eg, github), and let us know the location of the repository (preferably one day prior to your scheduled interview).

Be prepared to:

- Present your solution and walk through how you designed, implemented, and tested the program. During the technical interview, we may ask you to share your screen and show the program running.
- Discuss any assumptions you made and challenges you encountered.