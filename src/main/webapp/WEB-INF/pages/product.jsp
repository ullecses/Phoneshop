<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product Details">
  <p>
    ${product.description}
  </p>
  <table>
     <tr>
       <td>Image</td>
       <td>
         <img src="${product.imageUrl}">
       </td>
     </tr>
     <tr>
       <td>code</td>
       <td>
         ${product.code}
       </td>
     </tr>
     <tr>
       <td>price</td>
       <td>
         <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
       </td>
     </tr>
     <tr>
       <td>stock</td>
       <td>
         ${product.stock}
       </td>
     </tr>
  </table>

  <h2>История цен</h2>
  <table border="1">
    <tr>
        <th>Дата</th>
        <th>Цена</th>
        <th>Валюта</th>
    </tr>
    <c:forEach var="history" items="${product.priceHistory}">
        <tr>
            <td><fmt:formatDate value="${history.date}" pattern="dd.MM.yyyy" /></td>
            <td><fmt:formatNumber value="${history.price}" type="currency" currencySymbol="${history.currency.symbol}" /></td>
            <td>${history.currency.currencyCode}</td>
        </tr>
    </c:forEach>
  </table>

</tags:master>