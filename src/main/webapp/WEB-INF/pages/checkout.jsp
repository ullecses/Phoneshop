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
          <td colspan="3">Subtotal</td>
          <td colspan="2">
            <fmt:formatNumber value="${order.subtotal}" pattern="#,##0.00"/>
            <c:out value="${order.items[0].product.currency.symbol != null ? order.items[0].product.currency.symbol : '$'}"/>
          </td>
        </tr>
        <tr>
          <td colspan="3">Delivery cost</td>
          <td colspan="2">
            <fmt:formatNumber value="${order.deliveryCost}" pattern="#,##0.00"/>
            <c:out value="${order.items[0].product.currency.symbol != null ? order.items[0].product.currency.symbol : '$'}"/>
          </td>
        </tr>
        <tr>
          <td colspan="3">Total cost</td>
          <td colspan="2">
            <fmt:formatNumber value="${order.totalCost}" pattern="#,##0.00"/>
            <c:out value="${order.items[0].product.currency.symbol != null ? order.items[0].product.currency.symbol : '$'}"/>
          </td>
        </tr>
      </tbody>
    </table>

    <h2>Your details</h2>
    <table>
      <tags:orderFormRow name="firstName" label="First Name" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="lastName" label="Last Name" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="deliveryDate" label="Delivery Date" order="${order}" errors="${errors}"></tags:orderFormRow>
      <tags:orderFormRow name="deliveryAddress" label="Delivery Address" order="${order}" errors="${errors}"></tags:orderFormRow>

      <tr>
        <td>Payment Method<span style="color:red">*</span></td>
        <td>
          <select name="paymentMethod"/>
            <option></option>
            <c:forEach var="paymentMethod" items="${paymentMethods}">
            <option>${paymentMethod}</option>
            </c:forEach>
          </select>
          <c:set var="error" value="${errors['paymentMethod']}"/>
          <c:if test="${not empty error}">
            <div class="error">
              ${error}
            </div>
          </c:if>
        </td>
      </tr>
    </table>
    <p><button type="submit">Place order</button></p>
  </form>

  <form id="deleteCartItem" method="post"></form>
</tags:master>
