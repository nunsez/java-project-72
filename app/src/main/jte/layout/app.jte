@import gg.jte.Content
@import hexlet.code.dto.page.Page
@param Content content
@param Page page = null

<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We"
          crossorigin="anonymous">
    <title>Page Analyzer</title>
</head>
<body>
@if(page != null && page.flash() != null)
    <p>${page.flash()}</p>
@endif
${content}
</body>
</html>
