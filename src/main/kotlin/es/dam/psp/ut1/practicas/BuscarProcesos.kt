package es.dam.psp.ut1.practicas

import com.sun.tools.javac.tree.TreeInfo.args

// enunciado indica que recibe args: Arra
fun main(args: Array<String>) {

    // extrae el primer argumento introducido (por ejemplo, "java"). Si no se introduce ninguno, se queda vacío.
    val filtro = args.firstOrNull()?.lowercase() ?: "";
    println("Buscando procesos cuyo comando contenga: '$filtro'\n")

    // ProcessHandle. da información sobre cualquier proceso del sistema
    val yo = ProcessHandle.current()
    println("Soy el PID ${yo.pid()}, mi padre es ${yo.parent().map { it.pid() }.orElse(-1)}")
    println("%-8s %-8s %-12s %s".format("PID", "PPID", "USUARIO", "COMANDO"))

    ProcessHandle.allProcesses()
        .filter { p ->
            val info = p.info();
            // Comprobamos que el comando exista para evitar nulos
            if (!info.command().isPresent) return@filter false;
            // Si hay filtro, comprobamos que el comando contenga la cadena buscada
            if (filtro.isEmpty()) return@filter true
            info.command().get().lowercase().contains(filtro)
        }
        .forEach { p ->
            val info = p.info()
            // para conseguir la hora exacta a la que arranco el proceso
            val inicio = info.startInstant().map { it.toString() }.orElse("-")
            // Pregunta cuántos segundos o milisegundos de procesador ha consumido ese programa desde que empezó
            val cpu = info.totalCpuDuration().map { "${it.toMillis()} ms" }.orElse("-")
            println(
                "%-8d %-8s %-12s %s".format(
                    p.pid(),
                    p.parent().map { it.pid().toString() }.orElse("-"),   // "-" si no tiene padre visible
                    info.user().orElse("?").takeLast(12),                  // "?" si el SO no nos deja leerlo
                    inicio.take(25),
                    cpu,
                    info.command().get()
                )
            )
        }
}