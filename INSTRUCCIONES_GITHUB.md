# 🚀 Guía para compilar tu APK con GitHub Actions

## 📱 Desde tu teléfono Android (MÁS FÁCIL)

### Paso 1: Crear cuenta en GitHub (si no tienes)
1. Abre tu navegador y ve a https://github.com
2. Toca "Sign up" (Registrarse)
3. Ingresa tu email, crea una contraseña y elige un nombre de usuario
4. Verifica tu email

### Paso 2: Crear un nuevo repositorio
1. Ve a https://github.com/new
2. Nombre del repositorio: `NotificationRestorer`
3. Selecciona "Public" (Público)
4. NO marques ninguna opción de inicializar
5. Toca "Create repository"

### Paso 3: Subir el código

**Opción A: Usando la interfaz web de GitHub (MÁS FÁCIL)**

1. En la página de tu repositorio, toca "uploading an existing file"
2. Arrastra o selecciona el archivo **NotificationRestorerApp.zip**
3. GitHub lo descomprimirá automáticamente
4. Escribe un mensaje: "Initial commit"
5. Toca "Commit changes"

**Opción B: Usando GitHub Mobile App**

1. Descarga "GitHub" desde Google Play Store
2. Inicia sesión
3. Abre tu repositorio
4. Toca el ícono de menú (tres puntos)
5. Selecciona "Upload files"
6. Sube el archivo ZIP

### Paso 4: Esperar que compile (automático) ⏱️

1. Ve a tu repositorio en GitHub
2. Toca la pestaña "Actions" (en el menú superior)
3. Verás un workflow ejecutándose (punto amarillo 🟡)
4. Espera 5-10 minutos a que termine (punto verde ✅)

### Paso 5: Descargar tu APK 📥

1. Cuando el workflow termine (✅), toca en el nombre del workflow
2. Desplázate hacia abajo hasta "Artifacts"
3. Verás dos archivos:
   - `app-debug` - APK de depuración
   - `NotificationRestorer-v1.0` - APK con nombre descriptivo
4. Toca cualquiera para descargar (son el mismo archivo)
5. Se descargará un archivo .zip
6. Descomprime el ZIP y encontrarás `app-debug.apk`

### Paso 6: Instalar el APK 📲

1. Abre el archivo `app-debug.apk` desde tu administrador de archivos
2. Android te pedirá permiso para "Instalar apps desconocidas"
3. Permite la instalación
4. ¡Listo! Ya puedes abrir Notification Restorer

---

## 💻 Desde un PC (Alternativa)

### Opción 1: GitHub Desktop

1. Descarga GitHub Desktop: https://desktop.github.com
2. Inicia sesión con tu cuenta de GitHub
3. File → Add local repository → Selecciona la carpeta NotificationRestorerApp
4. Publish repository
5. Ve a GitHub.com → Actions → Espera la compilación
6. Descarga el APK de Artifacts

### Opción 2: Git por línea de comandos

```bash
cd NotificationRestorerApp
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/NotificationRestorer.git
git push -u origin main
```

Luego ve a Actions en GitHub.com

---

## 🔧 Si algo sale mal

### El workflow falla ❌

1. Ve a Actions → Toca el workflow fallido
2. Toca en el trabajo (job) que falló
3. Lee el error en los logs
4. Común: Permisos de Actions no habilitados
   - Ve a Settings → Actions → General
   - Permite "Read and write permissions"

### No puedo descargar el APK

- Los artifacts expiran en 90 días
- Si expiran, solo haz un nuevo commit para que compile de nuevo:
  - Edita el README.md
  - Añade una línea cualquiera
  - Commit → Se compilará automáticamente

### El APK no se instala

- Asegúrate de permitir "Instalar apps desconocidas" para tu navegador/administrador de archivos
- Configuración → Seguridad → Fuentes desconocidas

---

## 🎯 Actualizaciones futuras

Cada vez que hagas cambios al código:

1. Sube los archivos modificados a GitHub
2. Actions compilará automáticamente el nuevo APK
3. Descarga el nuevo APK de Artifacts

---

## 📝 Notas importantes

- ✅ La compilación es GRATIS en GitHub
- ✅ No necesitas instalar nada en tu teléfono
- ✅ GitHub hace todo el trabajo pesado
- ✅ Puedes compilar cuantas veces quieras
- ⚠️ El APK será de depuración (debug), no firmado para publicar en Play Store
- ⚠️ Los artifacts se eliminan después de 90 días (solo vuelve a compilar)

---

## 🆘 ¿Necesitas ayuda?

Si tienes problemas:
1. Revisa la pestaña "Actions" para ver errores
2. Lee los logs del workflow
3. Asegúrate de haber subido TODOS los archivos del proyecto
4. Verifica que el archivo `.github/workflows/build.yml` esté presente

---

## 🎉 ¡Eso es todo!

En unos minutos tendrás tu APK listo para instalar sin necesidad de Android Studio ni PC para compilar.
