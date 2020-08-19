package com.samsad.model

data class PostSnippet(val snippet: Text) {
    data class Text(val text: String)
}

/*

  curl \
  --request POST \
  --header "Content-Type: application/json" \
  --data '{"snippet" : {"text" : "mysnippet"}}' \
  http://127.0.0.1:8080/snippets

* */