<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageTitle" required="true" %>

<html>
<head>
  <title>${pageTitle}</title>
  <link href='http://fonts.googleapis.com/css?family=Lobster+Two' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="${pageContext.servletContext.contextPath}/styles/main.css">
  <style>
    header a[href*="/cart"] {
        font-size: 52px;
        color: #ff6600;
        text-decoration: none;
    }

    header a[href*="/cart"]:hover {
        color: #cc3300;
    }
  </style>
</head>
<body class="product-list">
  <header>
    <a href="${pageContext.servletContext.contextPath}">
      <img src="${pageContext.servletContext.contextPath}/images/logo.svg"/>
      PhoneShop
    </a>
    <a href="${pageContext.servletContext.contextPath}/cart">
      My cart
    </a>
  </header>
  <main>
    <jsp:doBody/>
  </main>
</body>
</html>
