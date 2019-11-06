# Java-Web-Server-HTTP-Requests

This program is an HTTP server written in Java.

## Installation

1. Clone the project: `git clone https://github.com/Adamouization/Java-Web-Server-HTTP-Requests`

2. Cd into the directory and compile the files, then run the source code:

```
cd Java-Web-Server-HTTP-Requests/src
javac WebServerMain.java
java WebServerMain <document_root> <port_number>
```

## Usage

### Server

`java AlignText <file_name> <line_length> [option]`

where:
* `document_root` is the path to the Plain Text file to wrap and align.
* `port_number` is the desired length of the line for wrapping the text.
    
Example: `java WebServerMain ../www 12345`

Run the tests: `stacscheck Tests/`

### Client

* Terminal
    * `curl -s -I -X GET localhost:12345/index.html`
    * `curl -s GET localhost:12345/index.html`
* Web browser
    * `open http://127.0.0.1:12345/`

## Javadocs

1. Generate the Javadocs: `javadoc -d javadoc src/*.java src/exception/*.java`

2. Open `javadoc/index.html` in your web browser.


## Contact
* email: adam@jaamour.com
* LinkedIn: [www.linkedin.com/in/adamjaamour](https://www.linkedin.com/in/adamjaamour/)
* website: [www.adam.jaamour.com](www.adam.jaamour.com)
* twitter: [@Adamouization](https://twitter.com/Adamouization)