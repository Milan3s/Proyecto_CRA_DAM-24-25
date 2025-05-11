package model;

import java.sql.Date;

public class Prestamo {
    private Dispositivo dispositivo;
    private Alumno alumno;
    private Date fecha_inicio;
    private Date fecha_fin;

    public Prestamo(Dispositivo dispositivo, Alumno alumno, Date fecha_inicio, Date fecha_fin) {
        this.dispositivo = dispositivo;
        this.alumno = alumno;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }
}
