import { createContext, useContext, useState } from 'react';

export const XP_POR_DIFICULTAD = { Fácil: 10, Media: 25, Difícil: 50 };
export const XP_POR_NIVEL = 100;

const MISIONES_INICIALES = [
  { id: '1', titulo: 'Estudiar para el examen parcial', descripcion: 'Repasar todos los temas del primer parcial.', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '2', titulo: 'Repasar los apuntes de clase', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '3', titulo: 'Tender la cama', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '4', titulo: 'Terminar el proyecto de la materia', descripcion: 'Dejar lista la entrega antes de la fecha límite.', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '5', titulo: 'Salir a caminar 20 minutos', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '6', titulo: 'Tomar un vaso de agua', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '7', titulo: 'Practicar código durante una hora', descripcion: 'Un ejercicio nuevo cada día.', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '8', titulo: 'Preparar la comida de la semana', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '9', titulo: 'Revisar el correo', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '10', titulo: 'Entregar la tarea de programación', descripcion: '', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '11', titulo: 'Ordenar el escritorio', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '12', titulo: 'Regar las plantas', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '13', titulo: 'Hacer ejercicio 45 minutos', descripcion: 'Rutina completa, sin saltarse el calentamiento.', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '14', titulo: 'Leer un capítulo de un libro', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '15', titulo: 'Sacar la basura', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '16', titulo: 'Lavar la ropa', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '17', titulo: 'Organizar la mochila', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
];

const MisionesContext = createContext();

export function MisionesProvider({ children }) {
  const [misiones, setMisiones] = useState(MISIONES_INICIALES);
  const [xpTotal, setXpTotal] = useState(0);

  const agregarMision = ({ titulo, descripcion, dificultad }) => {
    const nueva = {
      id: Date.now().toString(),
      titulo,
      descripcion,
      dificultad,
      xp: XP_POR_DIFICULTAD[dificultad],
      completada: false,
    };
    setMisiones((prev) => [nueva, ...prev]);
  };

  const editarMision = (id, cambios) => {
    setMisiones((prev) =>
      prev.map((m) => {
        if (m.id !== id) return m;
        const dificultad = cambios.dificultad ?? m.dificultad;
        return { ...m, ...cambios, xp: XP_POR_DIFICULTAD[dificultad] };
      })
    );
  };

  const eliminarMision = (id) => {
    setMisiones((prev) => prev.filter((m) => m.id !== id));
  };

  const limpiarCompletadas = () => {
    setMisiones((prev) => prev.filter((m) => !m.completada));
  };

  const completarMision = (id) => {
    const mision = misiones.find((m) => m.id === id);
    if (!mision || mision.completada) return;
    setMisiones((prev) =>
      prev.map((m) => (m.id === id ? { ...m, completada: true } : m))
    );
    setXpTotal((xp) => xp + mision.xp);
  };

  const nivel = Math.floor(xpTotal / XP_POR_NIVEL) + 1;
  const xpEnNivel = xpTotal % XP_POR_NIVEL;

  const value = {
    misiones,
    xpTotal,
    nivel,
    xpEnNivel,
    agregarMision,
    editarMision,
    eliminarMision,
    completarMision,
    limpiarCompletadas,
  };

  return <MisionesContext.Provider value={value}>{children}</MisionesContext.Provider>;
}

export function useMisiones() {
  const ctx = useContext(MisionesContext);
  if (!ctx) {
    throw new Error('useMisiones debe usarse dentro de MisionesProvider');
  }
  return ctx;
}