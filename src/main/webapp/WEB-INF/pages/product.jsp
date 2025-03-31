<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product Details">
  <p>
      Cart:${cart}
  </p>
        <c:if test= "${not empty param.message}">
            <div class = "param.success">
                ${message}
            </div>
        </c:if>

        <c:if test= "${not empty error}">
           <div class = "error">
               ${error}
           </div>
        </c:if>
  <p>
    ${product.description}
  </p>
  <form method="post">
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
         <tr>
             <td>quantity</td>
             <td>
                <input name = "quantity" value="${not empty error ? param.quantity : 1}" class ="quantity">
             </td>
             </tr>
      </table>
      <p>
        <button>Add to cart</button>
      </p>
  <form>

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

  <h2>Последние просмотренные товары</h2>
  <c:if test="${not empty recentProducts}">
      <table border="1">
          <tr>
              <th>Image</th>
              <th>Code</th>
              <th>Price</th>
          </tr>
          <c:forEach var="recent" items="${recentProducts}">
                      <tr>
                          <td>
                              <a href="${pageContext.request.contextPath}/products/${recent.id}">
                                  <img src="${recent.imageUrl}" width="50">
                              </a>
                          </td>
                          <td>
                              <a href="${pageContext.request.contextPath}/products/${recent.id}">
                                  ${recent.code}
                              </a>
                          </td>
                          <td>
                              <fmt:formatNumber value="${recent.price}" type="currency" currencySymbol="${recent.currency.symbol}"/>
                          </td>
                      </tr>
                  </c:forEach>
      </table>
  </c:if>


</tags:master>