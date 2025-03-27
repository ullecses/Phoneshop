<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<jsp:useBean id="priceHistoryMap" type="java.util.Map" scope="request"/>

<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <form>
   <input name="query" value="${param.query}">
   <button>Search</button>
  </form>
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
          Description
          <tags:sortLink sort="description" order="asc"/>
          <tags:sortLink sort="description" order="desc"/>
        </td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc"/>
          <tags:sortLink sort="price" order="desc"/>
        </td>
        <td>History</td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}">
        <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}" alt="${product.description}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
              ${product.description}
            </a>
          </td>
          <td class="price">
            <fmt:formatNumber value="${product.price != null ? product.price : 0}"
                              type="currency"
                              currencySymbol="${product.currency.symbol}"
                              pattern="#,##0.00"/>
          </td>
          <td>
            <button class="toggle-history" data-product-id="${product.id}">🔍</button>
            <div class="price-history" id="history-${product.id}" style="display: none;">
              <c:choose>
                <c:when test="${not empty priceHistoryMap[product.id]}">
                  <c:forEach var="price" items="${priceHistoryMap[product.id]}">
                    <div>
                      <fmt:formatNumber value="${price.price}"
                                        type="currency"
                                        currencySymbol="${product.currency.symbol}"
                                        pattern="#,##0.00"/>
                    </div>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <div>No price history available.</div>
                </c:otherwise>
              </c:choose>
            </div>
          </td>
        </tr>
      </c:forEach>
  </table>

  <script>
    document.addEventListener("DOMContentLoaded", function() {
      document.querySelectorAll(".toggle-history").forEach(button => {
        button.addEventListener("click", function() {
          let historyDiv = document.getElementById("history-" + this.dataset.productId);
          historyDiv.style.display = (historyDiv.style.display === "none") ? "block" : "none";
        });
      });
    });
  </script>

</tags:master>
