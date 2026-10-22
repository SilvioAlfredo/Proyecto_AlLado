echo -e "${YELLOW}--- 8. FAVORITOS: AGREGAR Y LISTAR ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/favoritos" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"usuarioId\": $USER_ID,
    \"trabajadorId\": 1
  }"
echo ""

curl -s -w "\n%{http_code}" -X GET "$BASE_URL/favoritos/usuario/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 9. VALORACIONES: CREAR Y LISTAR ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/valoraciones" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN" \
  -d "{
    \"puntuacion\": 5,
    \"comentario\": \"Excelente servicio, puntual y profesional.\",
    \"autorId\": 2,
    \"destinatarioId\": $USER_ID
  }"
echo ""

curl -s -w "\n%{http_code}" -X GET "$BASE_URL/valoraciones/usuario/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 10. SUSCRIPCIONES: CONSULTAR ESTADO ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/suscripciones/usuario/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 11. DONACIONES: REGISTRAR INTENTO ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/donaciones" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"monto\": 1500.0,
    \"metodoPago\": \"MERCADO_PAGO\",
    \"usuarioId\": $USER_ID
  }"
echo ""

echo -e "${YELLOW}--- 12. REPORTES Y MODERACIÓN ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/reportes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"motivo\": \"Spam en la descripción\",
    \"descripcion\": \"Publicación con enlaces externos sospechosos\",
    \"sector\": \"Norte\",
    \"reportanteId\": $USER_ID,
    \"reportadoId\": 2
  }"
echo ""

curl -s -w "\n%{http_code}" -X GET "$BASE_URL/reportes?sector=Norte" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN"
echo ""