import { createContext, useContext, useState } from 'react';

export const XP_POR_DIFICULTAD = { Fácil: 10, Media: 25, Difícil: 50 };
export const XP_POR_NIVEL = 100;

const MISIONES_INICIALES = [
  { id: '1', titulo: 'Estudiar para el examen', descripcion: '', dificultad: 'Difícil', xp: 50, completada: false },
  { id: '2', titulo: 'Hacer ejercicio', descripcion: '', dificultad: 'Media', xp: 25, completada: false },
  { id: '3', titulo: 'Tender la cama', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
  { id: '4', titulo: 'Leer 10 páginas', descripcion: '', dificultad: 'Fácil', xp: 10, completada: false },
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