<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>

<tags:master pageTitle="Checkout">
  <p>Cart: ${cart}</p>

  <c:if test="${not empty param.message}">
    <div class="success">${param.message}</div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="error">${errors[item.product.id]}</div>
  </c:if>

  <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
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
        <c:forEach var="item" items="${order.items}" varStatus="status">
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
            </td>
            <td>
              <fmt:formatNumber value="${item.product.price}" pattern="#,##0.00"/>
              <c:out value="${item.product.currency.symbol != null ? item.product.currency.symbol : '$'}"/>
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
    <h2>Your details</h2>
    <table>
      <tr>
        <td>First name<span style="color:red">*</span></td>
        <td>
          <c:set var="error" value="${errors['firstName']}"/>
          <input name="firstName" value="$not empty error ? param['firstName'] : order.firstName"/>
          <c:if test="${not empty error}">
            <div class="error">${errors}</div>
          </c:if>
        </td>
      </tr>
      <tr>
        <td>Last name<span style="color:red">*</span></td>
        <td><input name="lastName"/></td>
      </tr>
      <tr>
        <td>Phone<span style="color:red">*</span></td>
        <td><input name="phone"/></td>
      </tr>
      <tr>
        <td>Delivery Date<span style="color:red">*</span></td>
        <td><input name="deliveryDate"/></td>
      </tr>
      <tr>
        <td>Delivery Address<span style="color:red">*</span></td>
        <td><input name="deliveryAddress"/></td>
      </tr>
      <tr>
        <td>Payment Method<span style="color:red">*</span></td>
        <td><input name="paymentMethod"/></td>
      </tr>
    </table>
    <p><button type="submit">Update</button></p>
  </form>

  <form id="deleteCartItem" method="post"></form>
</tags:master>
