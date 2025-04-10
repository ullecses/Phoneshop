<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>

<tags:master pageTitle="Product Cart">
  <p>Cart: ${cart}</p>

  <c:if test="${not empty param.message}">
    <div class="success">${param.message}</div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="error">${errors[item.product.id]}</div>
  </c:if>

  <form method="post" action="${pageContext.servletContext.contextPath}/cart">
    <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>Description</td>
          <td class="quantity">Quantity</td>
          <td class="price">Price</td>
          <td>Action</td>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="item" items="${cart.items}" varStatus="status">
          <tr>
            <td>
              <img class="product-tile" src="${item.product.imageUrl}" alt="Product Image"/>
            </td>
            <td>
              <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                ${item.product.description}
              </a>
            </td>
            <td class="quantity">
              <c:set var="error" value="${errors[item.product.id]}"/>
              <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}" class="quantity"/>
              <c:if test="${not empty error}">
                <div class="error">${errors[item.product.id]}</div>
              </c:if>
              <input type="hidden" name="productId" value="${item.product.id}"/>
            </td>
            <td>
              <fmt:formatNumber value="${item.product.price}" pattern="#,##0.00"/>
              <c:out value="${item.product.currency.symbol != null ? item.product.currency.symbol : '$'}"/>
            </td>
            <td>
              <button form="deleteCartItem"
                      formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                Delete
              </button>
            </td>
          </tr>
        </c:forEach>
        <tr>
          <td colspan="3">Total cost</td>
          <td colspan="2">
            <fmt:formatNumber value="${cart.totalCost}" pattern="#,##0.00"/>
            <c:out value="${cart.items[0].product.currency.symbol != null ? cart.items[0].product.currency.symbol : '$'}"/>
          </td>
        </tr>
      </tbody>
    </table>

    <p><button type="submit">Update</button></p>
  </form>

  <form id="deleteCartItem" method="post"></form>
</tags:master>
