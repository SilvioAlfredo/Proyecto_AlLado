#!/bin/bash
# AlLado API - cURL Commands for Testing
# Usage: source this file or copy-paste commands
# Base URL
BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== AlLado API Testing with cURL ===${NC}"
echo "Base URL: $BASE_URL"
echo ""

# ============================================
# 1. AUTENTICACIÓN
# ============================================

echo -e "${YELLOW}--- 1. REGISTRO TRABAJADOR ---${NC}"
REGISTER_TRABAJADOR_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "trabajador@test.com",
    "password": "password123",
    "telefono": "+5491123456789",
    "rol": "TRABAJADOR"
  }')
HTTP_CODE=$(echo "$REGISTER_TRABAJADOR_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$REGISTER_TRABAJADOR_RESPONSE" | head -n -1)
echo "Status: $HTTP_CODE"
echo "Response: $RESPONSE_BODY"
TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo -e "${GREEN}Token guardado: ${TOKEN:0:20}...${NC}"
echo ""

echo -e "${YELLOW}--- 2. REGISTRO EMPLEADOR ---${NC}"
REGISTER_EMPLEADOR_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Empresa ABC",
    "email": "empleador@test.com",
    "password": "password123",
    "telefono": "+5491198765432",
    "rol": "EMPLEADOR"
  }')
HTTP_CODE=$(echo "$REGISTER_EMPLEADOR_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$REGISTER_EMPLEADOR_RESPONSE" | head -n -1)
echo "Status: $HTTP_CODE"
echo "Response: $RESPONSE_BODY"
EMPLEADOR_TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo -e "${GREEN}Empleador Token: ${EMPLEADOR_TOKEN:0:20}...${NC}"
echo ""

echo -e "${YELLOW}--- 3. LOGIN TRABAJADOR ---${NC}"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "trabajador@test.com",
    "password": "password123"
  }')
HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$LOGIN_RESPONSE" | head -n -1)
echo "Status: $HTTP_CODE"
echo "Response: $RESPONSE_BODY"
TOKEN=$(echo "$RESPONSE_BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
USER_ID=$(echo "$RESPONSE_BODY" | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo -e "${GREEN}Token: ${TOKEN:0:20}...${NC}"
echo -e "${GREEN}User ID: $USER_ID${NC}"
echo ""

echo -e "${YELLOW}--- 4. VERIFICAR EMAIL (placeholder) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/auth/verify-email?token=test-token" \
  -H "Content-Type: application/json"
echo ""

echo -e "${YELLOW}--- 5. CAMBIAR CONTRASEÑA ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/password" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "trabajador@test.com",
    "currentPassword": "password123",
    "newPassword": "newpassword456"
  }'
echo ""

echo -e "${YELLOW}--- 6. VERIFICAR CONTRASEÑA ACTUAL ---${NC}"
curl -s -w "\n%{http_code}" -X POST "$BASE_URL/auth/password/verify" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "trabajador@test.com",
    "currentPassword": "newpassword456"
  }'
echo ""

# ============================================
# 2. OPORTUNIDADES LABORALES
# ============================================

echo -e "${YELLOW}=== OPORTUNIDADES LABORALES ===${NC}"
echo ""

echo -e "${YELLOW}--- 1. CREAR OFERTA (como empleador) ---${NC}"
CREATE_OFERTA_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/oportunidades" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN" \
  -d "{
    \"titulo\": \"Electricista para mantenimiento\",
    \"contenido\": \"Se necesita electricista certificado para mantenimiento preventivo en edificio de oficinas.\",
    \"ubicacion\": \"Buenos Aires, CABA\",
    \"modalidad\": \"PRESENCIAL\",
    \"rangoSalarial\": \"\$150.000 - \$200.000\",
    \"autor\": {\"id\": 2}
  }")
HTTP_CODE=$(echo "$CREATE_OFERTA_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$CREATE_OFERTA_RESPONSE" | head -n -1)
echo "Status: $HTTP_CODE"
echo "Response: $RESPONSE_BODY"
OFERTA_ID=$(echo "$RESPONSE_BODY" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo -e "${GREEN}Oferta ID: $OFERTA_ID${NC}"
echo ""

echo -e "${YELLOW}--- 2. LISTAR OFERTAS (paginado, sin filtro) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/oportunidades?page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 3. LISTAR OFERTAS (con filtro por especialidad) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/oportunidades?filtro=electricidad&page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 4. OBTENER OFERTA POR ID ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/oportunidades/$OFERTA_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 5. ACTUALIZAR OFERTA ---${NC}"
curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/oportunidades/$OFERTA_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN" \
  -d "{
    \"titulo\": \"Electricista para mantenimiento - ACTUALIZADO\",
    \"contenido\": \"Se necesita electricista certificado para mantenimiento preventivo y correctivo.\",
    \"ubicacion\": \"Buenos Aires, CABA\",
    \"modalidad\": \"HIBRIDO\",
    \"rangoSalarial\": \"\$160.000 - \$220.000\",
    \"autor\": {\"id\": 2}
  }"
echo ""

echo -e "${YELLOW}--- 6. ELIMINAR OFERTA ---${NC}"
# curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/oportunidades/$OFERTA_ID" \
#   -H "Content-Type: application/json" \
#   -H "Authorization: Bearer $EMPLEADOR_TOKEN"
echo "Comentado para preservar la oferta para tests de postulación"
echo ""

# ============================================
# 3. POSTULACIONES
# ============================================

echo -e "${YELLOW}=== POSTULACIONES ===${NC}"
echo ""

echo -e "${YELLOW}--- 1. CREAR POSTULACIÓN (como trabajador) ---${NC}"
CREATE_POSTULACION_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/postulaciones" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"titulo\": \"Postulación para Electricista\",
    \"contenido\": \"Tengo 5 años de experiencia en mantenimiento eléctrico industrial.\",
    \"presupuesto\": 180000,
    \"disponibilidad\": \"Mañana\",
    \"autor\": {\"id\": $USER_ID},
    \"oportunidadLaboral\": {\"id\": $OFERTA_ID}
  }")
HTTP_CODE=$(echo "$CREATE_POSTULACION_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$CREATE_POSTULACION_RESPONSE" | head -n -1)
echo "Status: $HTTP_CODE"
echo "Response: $RESPONSE_BODY"
POSTULACION_ID=$(echo "$RESPONSE_BODY" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo -e "${GREEN}Postulación ID: $POSTULACION_ID${NC}"
echo ""

echo -e "${YELLOW}--- 2. OBTENER POSTULACIÓN POR ID ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/postulaciones/$POSTULACION_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 3. LISTAR TODAS LAS POSTULACIONES (admin) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/postulaciones" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 4. SELECCIONAR POSTULACIÓN (PATCH) ---${NC}"
curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/postulaciones/$POSTULACION_ID/seleccionar" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN"
echo ""

echo -e "${YELLOW}--- 5. RECHAZAR POSTULACIÓN (PATCH) ---${NC}"
# Crear otra postulación para rechazar
CREATE_POSTULACION_2=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/postulaciones" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"titulo\": \"Postulación 2 para Electricista\",
    \"contenido\": \"Otra postulación para test de rechazo.\",
    \"presupuesto\": 170000,
    \"disponibilidad\": \"Tarde\",
    \"autor\": {\"id\": $USER_ID},
    \"oportunidadLaboral\": {\"id\": $OFERTA_ID}
  }")
POSTULACION_ID_2=$(echo "$CREATE_POSTULACION_2" | head -n -1 | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "Postulación 2 ID: $POSTULACION_ID_2"

curl -s -w "\n%{http_code}" -X PATCH "$BASE_URL/postulaciones/$POSTULACION_ID_2/rechazar" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN"
echo ""

echo -e "${YELLOW}--- 6. ELIMINAR POSTULACIÓN (debe fallar - inmutable) ---${NC}"
curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/postulaciones/$POSTULACION_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

# ============================================
# 4. USUARIOS / PERFILES
# ============================================

echo -e "${YELLOW}=== USUARIOS / PERFILES ===${NC}"
echo ""

echo -e "${YELLOW}--- 1. OBTENER PERFIL USUARIO (genérico) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/usuarios/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 2. ACTUALIZAR PERFIL USUARIO ---${NC}"
curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/usuarios/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"nombre\": \"Juan Pérez Actualizado\",
    \"email\": \"trabajador@test.com\",
    \"telefono\": \"+5491123456789\",
    \"password\": \"\"
  }"
echo ""

echo -e "${YELLOW}--- 3. LISTAR TODOS LOS USUARIOS (admin) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/usuarios" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 4. OBTENER PERFIL TRABAJADOR (detallado) ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/trabajadores/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo -e "${YELLOW}--- 5. ACTUALIZAR PERFIL TRABAJADOR ---${NC}"
curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/trabajadores/$USER_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"nombre\": \"Juan Pérez\",
    \"email\": \"trabajador@test.com\",
    \"telefono\": \"+5491123456789\",
    \"fotografia\": \"https://example.com/foto.jpg\",
    \"biografia\": \"Electricista con 10 años de experiencia\",
    \"certificados\": \"Certificado electricista matriculado N° 12345\",
    \"especialidades\": [
      {\"nombre\": \"Electricidad\"},
      {\"nombre\": \"Mantenimiento Industrial\"}
    ]
  }"
echo ""

echo -e "${YELLOW}--- 6. OBTENER PERFIL EMPLEADOR ---${NC}"
curl -s -w "\n%{http_code}" -X GET "$BASE_URL/empleadores/2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN"
echo ""

echo -e "${YELLOW}--- 7. ACTUALIZAR PERFIL EMPLEADOR ---${NC}"
curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/empleadores/2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $EMPLEADOR_TOKEN" \
  -d "{
    \"nombre\": \"Empresa ABC Actualizada\",
    \"email\": \"empleador@test.com\",
    \"telefono\": \"+5491198765432\"
  }"
echo ""

echo -e "${GREEN}=== Tests completados ===${NC}"
echo ""
echo "Variables disponibles para uso manual:"
echo "  TOKEN=$TOKEN"
echo "  EMPLEADOR_TOKEN=$EMPLEADOR_TOKEN"
echo "  USER_ID=$USER_ID"
echo "  OFERTA_ID=$OFERTA_ID"
echo "  POSTULACION_ID=$POSTULACION_ID"