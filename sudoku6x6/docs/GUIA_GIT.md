# Guía de Git y GitHub (≥ 2 commits y ≥ 2 Pull Requests por estudiante)

## Configuración inicial (una persona)
```bash
cd sudoku6x6
git init
git add .
git commit -m "chore: estructura inicial del proyecto Maven + JavaFX"
git branch -M main
git remote add origin https://github.com/USUARIO/sudoku6x6.git
git push -u origin main
```
Los demás integrantes: `git clone` y trabajar en ramas propias. Proteger `main` exigiendo Pull Request (Settings > Branches).

## Flujo por Pull Request
```bash
git checkout -b feature/nombre-de-la-funcionalidad
# ... cambios ...
git add .
git commit -m "feat: descripción corta"
git push -u origin feature/nombre-de-la-funcionalidad
```
Abrir el PR en GitHub, pedir revisión a un compañero, hacer *Merge*, y luego `git checkout main && git pull`.

## Reparto sugerido (2 integrantes, 2 PR cada uno)
| PR | Rama | Contenido | Autor |
|----|------|-----------|-------|
| 1 | `feature/modelo` | `Celda`, `Tablero`, `GeneradorSudoku` + Javadoc | Estudiante A |
| 2 | `feature/validacion-ayuda` | `recalcularErrores`, `describirConflicto`, `calcularSugerencia` | Estudiante A |
| 3 | `feature/vista` | FXML y CSS (Scene Builder) | Estudiante B |
| 4 | `feature/controlador-eventos` | `SudokuController`, `CeldaEventAdapter` | Estudiante B |

Si son 3 o más, repartan más PR pequeños (p. ej. ayuda, estilos, mensajes) para que cada uno tenga al menos 2 commits y 2 PR a su nombre.
Usen mensajes con prefijos (`feat:`, `fix:`, `docs:`, `style:`).
