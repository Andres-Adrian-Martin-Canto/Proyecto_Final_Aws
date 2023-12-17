<%-- 
    Document   : mostrarResultado
    Created on : 14 dic 2023, 03:15:53
    Author     : Andres Martin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
    <title>Google Maps Street View</title>
    <script src="https://maps.googleapis.com/maps/api/js?key=APIGOOGLEMAPS&callback=initMap&libraries=streetview"></script>
    <style>
        #street-view {
            height: 400px;
            width: 100%;
        }
    </style>
    <script>
        function initMap() {
            var latitude = <%= request.getParameter("latitud") %>;
            var longitude = <%= request.getParameter("longitud") %>;
            console.log(latitude);
            console.log(longitude);

            var panorama = new google.maps.StreetViewPanorama(
                document.getElementById('street-view'),
                {
                    position: {lat: parseFloat(latitude), lng: parseFloat(longitude)},
                    pov: {heading: 165, pitch: 0},
                    zoom: 1
                }
            );

            // Asigna el panorama al mapa
            var map = new google.maps.Map(document.getElementById('street-view'), {
                streetView: panorama
            });
        }
    </script>
</head>
<body>
    <h1>Ubicacion: <%= request.getParameter("ciudad") %></h1>
    <p><%= request.getParameter("descripcion") %></p>
    <div id="street-view"></div>
</body>
</html>
