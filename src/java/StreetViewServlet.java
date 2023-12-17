
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Andres Martin
 */
@WebServlet(urlPatterns = {"/StreetViewServlet"})
public class StreetViewServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet StreetViewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StreetViewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtengo el request que me enviaron desde el formulario
        String ciudad = request.getParameter("ciudad");
        String estado = request.getParameter("estado");
        String pais = request.getParameter("pais");
        // Mensaje para obtener coordenadas
        String messageCoordenadas = "Dame las coordenadas de grados, minutos y segundos a formato decimal del lugar " + ciudad +" pero la longitud en negativo cuando sea lugares del continente americano pero no omitas el simbolo";
        // Mensaje para obtener mensaje.
        String messageDescripcion = "Dame la descripcion de "+ ciudad;
        // Hablar al metodo OpenIa para obtener la descripcion.
        String descripcion = openIa(messageDescripcion, 1);
        // Hablar al metodo OpenIa para obtener las coordenadas.
        String coordenas = openIa(messageCoordenadas, 2);
        
        // Array para dividir las cordenadas:
        String[] partes = coordenas.split(",");
        String latitud = partes[0].trim(); 
        String longitud = partes[1].trim();

        System.out.println(latitud + " " + longitud);

        // Redirige a la página JSP con los parámetros
        response.sendRedirect("mostrarResultado.jsp?latitud=" + latitud + "&longitud=" + longitud + "&descripcion=" + descripcion + "&ciudad=" + ciudad);
    }

    public String openIa(String message, int operacion) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "Api_OpenIA"; // Reemplazar con tu clave de API
//        String model = "gpt-4 turbo";
        String model = "gpt-3.5-turbo-16k";

        int buscarCoordenadasoDescripcion = -1;
        while (buscarCoordenadasoDescripcion < 0) {
            try {
                Thread.sleep(3000); // 1000 milisegundos = 1 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "Bearer " + apiKey);
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                // Crear cuerpo de solicitud
                String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}], \"temperature\": 0, \"max_tokens\": 128, \"top_p\": 0, \"frequency_penalty\": 0, \"presence_penalty\": 0}";
                try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
                    writer.write(body);
                    writer.flush();
                }

                // Obtener respuesta
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject jsonObject = new JSONObject(response.toString());
                    // Obtener el array "choices" directamente
                    JSONArray choicesArray = jsonObject.getJSONArray("choices");

                    // Obtener el primer objeto del array
                    JSONObject firstChoice = choicesArray.getJSONObject(0);

                    // Acceder a la propiedad "message" dentro de firstChoice
                    JSONObject messageObject = firstChoice.getJSONObject("message");

                    // Acceder a la propiedad "content" dentro de messageObject
                    String content = messageObject.getString("content");

                    if (operacion == 1) {
//                        System.out.println("Entro en la operacion descripcion");
                        buscarCoordenadasoDescripcion++;
                        return content;
                    } else {
                        // Saca latitud y longitud
                        String latitudPattern = "Latitud: (-?\\d+\\.\\d+)° ([NS])";
                        String longitudPattern = "Longitud: (-?\\d+\\.\\d+)° ([WEO])";

                        Pattern latitudRegex = Pattern.compile(latitudPattern);
                        Pattern longitudRegex = Pattern.compile(longitudPattern);

                        Matcher latitudMatcher = latitudRegex.matcher(content);
                        Matcher longitudMatcher = longitudRegex.matcher(content);

                        if (latitudMatcher.find() && longitudMatcher.find()) {
                            String latitud = latitudMatcher.group(1);
                            String longitud = longitudMatcher.group(1);

                            return latitud + ", " + longitud;
                        } else {
                            System.out.println("No se encontraron coordenadas en el formato especificado.");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al realizar la solicitud a la API de OpenAI", e);
            }
        }

        return "";
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
