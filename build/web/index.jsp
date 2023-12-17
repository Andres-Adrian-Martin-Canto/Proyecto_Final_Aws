<%-- 
    Document   : index
    Created on : 14 dic 2023, 02:17:06
    Author     : Andres Martin
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Formulario para Street View</title>
</head>
<body>
    <h1>Buscar lugares:</h1>
    
    <form action="StreetViewServlet" method="post">
    <label for="ciudad">Ciudad:</label>
    <input type="text" id="ciudad" name="ciudad" required>
    <label for="estado">Estado:</label>
    <input type="text" id="estado" name="estado" required>
    <label for="pais">pais:</label>
    <input type="text" id="pais" name="pais" required>
    
    <button type="submit">Buscar</button>
</form>

</body>
</html>


