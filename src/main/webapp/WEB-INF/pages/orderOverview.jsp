<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>

<tags:master pageTitle="Order overview">
  <h2>Order overview</h2>
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
      <tags:orderOverviewRow name="firstName" label="First Name" order="${order}"></tags:orderFormRow>
      <tags:orderOverviewRow name="lastName" label="Last Name" order="${order}"></tags:orderFormRow>
      <tags:orderFormRow name="phone" label="Phone" order="${order}"></tags:orderFormRow>
      <tags:orderOverviewRow name="deliveryDate" label="Delivery Date" order="${order}"></tags:orderFormRow>
      <tags:orderOverviewRow name="deliveryAddress" label="Delivery Address" order="${order}"></tags:orderFormRow>

      <tr>
        <td>Payment Method<span style="color:red">*</span></td>
        <td>
          ${order.paymentMethod}
          <c:set var="error" value="${errors['paymentMethod']}"/>
          <c:if test="${not empty error}">
            <div class="error">
              ${error}
            </div>
          </c:if>
        </td>
      </tr>
    </table>
    <p><button type="submit">Update</button></p>
  </form>

</tags:master>
