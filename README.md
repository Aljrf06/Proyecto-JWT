# 🔐 Login con Spring Boot + JWT
 
Este proyecto es un sistema de autenticación dividido en dos partes: un backend desarrollado en Spring Boot que expone una API REST protegida con JWT, 
y un frontend desarrollado en HTML, CSS y JavaScript puro que consume esa API usando la función nativa `fetch` del navegador.

## 📁 Estructura del Proyecto
<img width="481" height="876" alt="image" src="https://github.com/user-attachments/assets/315f0892-8395-4601-a811-3f9bd6206ccd" />
<img width="484" height="220" alt="image" src="https://github.com/user-attachments/assets/baba10b9-47fb-4e0a-a319-3921342219aa" />

## ✅ ¿Cómo funciona el flujo?

1. El usuario se registra → Spring encripta la contraseña con BCrypt y la guarda en H2
2. El usuario inicia sesión → Spring verifica la contraseña y genera un token JWT
3. El frontend guarda el token en localStorage
4. En cada petición protegida, el frontend envía el token en el header Authorization
5. El servidor valida el token y permite o bloquea el acceso

## 📸 Captura de pantalla resultado final 
- Pantalla registro nuevo usuario
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/cefba989-c776-451c-a6ab-a2683fb9d70e" />

- Pantalla inicio sesión
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/6db9e81f-e491-493a-b957-2c390a3ffaa0" />

- Pantalla inicio sesión exitoso - Home
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/be83ea04-a569-4037-b37e-4b60d841b0b1" />

## 👨‍💻 Autores

- Alejandra Rodríguez Forero
- Jerson Steven Mantilla Ramirez 
