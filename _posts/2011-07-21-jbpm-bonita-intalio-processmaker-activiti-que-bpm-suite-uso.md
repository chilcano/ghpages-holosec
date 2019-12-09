---
layout:     post
title:      'jBPM, Bonita, Intalio, ProcessMaker, Activiti. Qué BPM Suite uso?'
date:       2011-07-20 23:09:05
categories: ['BPM']
tags:       ['activiti', 'Bonita', 'Intalio', 'jbpm', 'processmaker']
status:     publish 
permalink:  "/2011/07/21/jbpm-bonita-intalio-processmaker-activiti-que-bpm-suite-uso/"
---
Ultimamente, en los cursos que suelo impartir o durante la etapa de consultoría con algún cliente me preguntan ¿Qué BPM me recomendarías? o, he comprado un CMS y viene con un Workflow, podría crear un Sistema de Gestión de Expedientes?  
Y mi respuesta siempre es, sabes qué es un BPM?, sabes lo que implicaría construir aplicaciones siguiendo BPM en tu organización?. Pues la respuesta es muchas veces "no". En esta situación, siempre lo mejor es aclarar los conceptos e  
iniciar algún proceso de evaluación de tecnologías, casos de éxitos, costes económicos y las consecuencias.

[caption id="" align="alignnone" width="389" caption="jBPM, Bonita, Intalio, ProcessMaker and Activiti"][![jBPM, Bonita, Intalio, ProcessMaker and Activiti]({{ site.baseurl }}/assets/bpmsfoss_0logos.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_0logos.png)[/caption]
Este post es el primero de una serie de posts relacionados con BPM Free/Open Source. En este primero haremos una presentación rápida de las alternativas tecnológicas de los BPMs free/open source de mayor actividad y hacernos una idea  
de cuál o cuáles son los más adecuado para cada escenario que nos encontremos en nuestras Organizaciones.  
En los siguientes posts entraremos en detalle en cada uno de las herramientas BPM, inclusive haciendo alguna prueba de concepto con cada uno de ellos.
Antes de iniciar con la revisión, describiré qué elementos o componentes importantes existen en una Suite BPM y unas definiciones.

## 1\. Definiciones y conceptos

**BPM: Business Process Management.**

Metodología que permite analizar el comportamiento de la organización a través de los procesos.  
De Wikipedia (http://es.wikipedia.org/wiki/Gesti%C3%B3n_de_procesos_de_negocio)  
"Se llama Gestión de procesos de negocio (Business Process Management o BPM en inglés) a la metodología corporativa cuyo objetivo es mejorar la eficiencia a través de la gestión de los procesos de negocio, que se deben modelar, organizar, documentar y optimizar de forma continua. Como su nombre sugiere, BPM se enfoca en la administración de los procesos dentro de una organización."

**BPMS: Buiness Process Management System or Suite.**

Un conjunto de herramientas o componentes que busca automatizar la construcción de aplicaciones siguiendo la metodología BPM.

## 2\. Componentes en una Suite BPM

 **1\. Workflow:**  
Es el motor que ejecuta/orquesta los procesos de negocio definidos, lo hace siguiendo un lenguaje que el motor entiende, comunmente es BPEL.
 **2\. Process Designer:**  
Es la herramienta que permite definir los procesos de negocio usando una simbología o lenguaje natural propio de BPM.  
Este lenguaje puede ser BPMN o XPDL, algunos también suelen considerar BPEL.
 **3\. Form Creator:**  
La herramienta que me permite definir los formularios de interacción humana, es decir, formularios donde el usuario/persona puede iniciar, rechazar, aprobar, etc, es decir, interactuar con una instancia de un proceso de negocio.
 **4\. Business Activity Monitoring (BAM):**  
De la Wikipedia (http://en.wikipedia.org/wiki/Business_activity_monitoring):  
"... is software that aids in monitoring of business activities, as those activities are implemented in computer systems."
Los elementos de un BAM son: KPI's (indicadores claves de rendimiento), Dashboard (consola que permite monitorizar en tiempo real el valor actual de los KPI's para tomar decisiones).  
En mi opinión, el concepto de BI (Business Intelligence) engloba a BAM. Mientras que BAM aplica a las Organizaciones, BI es una disciplina y conjunto de herramientas que aplica a diferentes escenarios.
 **5\. Business Rules Engine (BRE):**  
De la Wikipedia (http://en.wikipedia.org/wiki/Business_rules_engine):  
"... is a software system that executes one or more business rules in a runtime production environment. The rules might come from legal regulation ("An employee can be fired for any reason or no reason but not for an illegal reason"), company policy ("All customers that spend more than $100 at one time will receive a 10% discount"), or other sources. A business rule system enables these company policies and other operational decisions to be defined, tested, executed and maintained separately from application code."
 **6\. Connectors:**  
\- Para ECM  
\- Para LDAP  
\- Para ESB, etc.
Son aquellos componentes que hacen que los BPMS tengan más sentido dentro de la Organización, por ejemplo, un sistema de gestión de expedientes donde su motor sea un workflow no tiene sentido sin un repositorio documental para alojar los documentos entrantes y salientes.

## 3\. BPMS/Workflows Free/Open Source

Existen muchos Workflows engine, BPMs free/open source y comerciales, nos centraremos únicamente en los proyectos FOSS y que de alguna forma están formados por los componentes antes indicados.
Pero por si os de interés, la lista de BPMS/Workflows FOSS actualizada (basada en "Open Source Workflow Engines in Java" - http://java-source.net/open-source/workflow-engines) al 2009 es esta:

[caption id="" align="alignnone" width="405" caption="BPMS FOSS list - 2009"][![BPMS FOSS list - 2009]({{ site.baseurl }}/assets/www.INTIX.info-LIST-BPMS-FOSS-2009.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/www.INTIX.info-LIST-BPMS-FOSS-2009.png)[/caption]

[Download (XLS) Lista aquí](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/www.INTIX.info-LIST-BPMS-FOSS-2009.xls).

## 4\. BPMS Free/Open Source

Iniciaré una revisión con los BPMS siguientes, ya que en mi opinión, estos son los proyectos más activos en este momento:
* 1\. jBPMEs el BPMS más veterano, Red Hat está detrás de esta iniciativa y en el 2010 hubo un cambio en el equipo de proyecto, esto dio paso al uso de Drools como BPM engine dentro de la versión 5.  
También dió paso al nacimiento de otro proyecto BPMS llamado Activiti.  
Aunque es un proyecto relativamente de un uso muy intensivo, la versión 5, es muy joven.
* 2\. BonitaBPMS que sin mucho aspavientos, resuelve con creces los objetivos de cualquier BPMS de esta época: Social, Colaboración y Zero Code.
* 3\. IntalioOtro proyecto veterano y equiparable a los BPMS comerciales, aunque la fuerza de su comunidad es muy baja.
* 4\. ProcessMakerEs BPMS muy versátil y eficaz. Hace lo que la gran mayoría de las PYMES necesitan. Es el único en esta lista construída en PHP.
* 5\. ActivitiEs el proyecto más joven de los evaluados, a nivel técnico el proyecto es muy prometedor aunque le falta posicionarse en el sector de los BPMS.
Para tener una noción de qué BPMS es la más adecuada para nuestra organización, es necesario revisar y valorar cuantitativamente cada una de ellas.  
La valoración cuantitativa la hago con una escala de 0 (no tiene o no existe) a 5 (cumple al 100%) para la existencia de ciertas características y funcionalidades de la tabla siguiente:

[caption id="" align="alignnone" width="350" caption="List of BPMS features to evaluate"][![List of BPMS features to evaluate]({{ site.baseurl }}/assets/bpmsfoss_1featureslist.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_1featureslist.png)[/caption]
Entonces, la valoración quedaría así:

[caption id="" align="alignnone" width="428" caption="Evaluation of jBPM"][![Evaluation of jBPM]({{ site.baseurl }}/assets/bpmsfoss_2eval-jbpm-rev2.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_2eval-jbpm-rev2.png)[/caption]

[caption id="" align="alignnone" width="426" caption="Evaluation of Bonita Open Solution"][![Evaluation of Bonita Open Solution]({{ site.baseurl }}/assets/bpmsfoss_3eval-bonita.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_3eval-bonita.png)[/caption]

[caption id="" align="alignnone" width="427" caption="Evaluation of Intalio|BPMS Community Edition"][![Evaluation of Intalio|BPMS Community Edition]({{ site.baseurl }}/assets/bpmsfoss_4eval-intalio.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_4eval-intalio.png)[/caption]

[caption id="" align="alignnone" width="426" caption="Evaluation of ProcessMaker"][![Evaluation of ProcessMaker]({{ site.baseurl }}/assets/bpmsfoss_5eval-processmaker.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_5eval-processmaker.png)[/caption]

[caption id="" align="alignnone" width="431" caption="Evaluation of Activiti"][![Evaluation of Activiti]({{ site.baseurl }}/assets/bpmsfoss_6eval-activiti.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_6eval-activiti.png)[/caption]

## Conclusiones

Después de valorar cuantitativamente cada una de los BPMS, concluímos:
1. Si el día de mañana tenemos que iniciar un proyecto siguiendo BPM que requiera el uso de los estándares, que sea posible integrarlo a las diferentes aplicaciones legadas y que el volumen de usuarios es muy alto, entonces Bonita Open Solution es nuestra alternativa.  
Partimos también que si iniciamos un proyecto nuevo dentro de la organización, es necesario contar con información técnica actualizada y abundantes ejemplos que puedan ayudar a cómo se construir rápidamente aplicaciones BPM.
2. Si nuestra organización es una PYME, Bonita Open Solution y el resto de BPMS quedan algo grande excepto ProcessMaker. Al usarlo no se pierde nada valioso, todas las funcionalidades que BPMS comerciales y las otras analizadas, ProcessMaker las tiene.  
Si el volumen de usuarios se ve incrementado, no hay problema, ProcessMaker puede ser escalado sin ningún problema o si deseas puedes contratar la versión SaaS alojado en Amazon EC2.
3. Para usar jBPM debemos esperar que la fuerza de su comunidad crezca, abundante y exacta información técnica exista, aunque si las organizaciones valoran la buena integración con Drools, entonces jBPM es la mejor alternativa.
4. Activiti, al igual que jBPM, aunque inicien con versiones superiores o iguales a 5.1, los pocos o ninguno casos de éxitos, la poca documentación técnica del producto se traduce en poca confianza en el producto, pues Activiti no es una buena alternativa.  
Técnicamente, Activiti es de lo mejor, al ser parte del proyecto Alfresco ECM, es una cuestión de tiempo para que se convierta en el BPM de referencia.
5. Intalio comparado a los 4 BPMS restantes es otra buena alternativa, aunque la incorporación de funcionalidades muy importantes en la versión Enteprise Edition disuade su uso.  
Por otro lado, al igual que la gran mayoría de productos con licenciamiento dual (Open Source y Comercial) no tiene documentación exacta, quedando únicamente los foros públicos como medio para solventar dudas.

[caption id="" align="alignnone" width="349" caption="jBPM vs. Bonita vs. Intalio vs. ProcessMaker vs. Activiti"][![jBPM vs. Bonita vs. Intalio vs. ProcessMaker vs. Activiti]({{ site.baseurl }}/assets/bpmsfoss_7resultado_rev2.png)](http://dl.dropbox.com/u/2961879/blog20110708_bpmfoss/bpmsfoss_7resultado_rev2.png)[/caption]

## Observaciones

Este artículo expresa mi personal opinión acerca de estos proyectos resultado de haber pasado por varios procesos de revisión y conocimiento de cada BPMS antes de iniciar un proyecto de desarrollo.
Entender que esta revisión pretende ser una forma rápida para identificar la solución BPMS que se ajusta a mis necesidades.
No pretender ser una referencia absoluta, ni nunca lo será.
El resultado de la evaluación está relacionado a mi conocimiento del producto, que es fruto de haber usado dichas suites en proyectos reales y pruebas de concepto.
En los siguientes artículos entraré en mayor detalle en cada uno de ellos, comparando características concretas como por ejemplo, el nivel de implementación de BPMN2 o la tecnología usada para generación automática de formularios, etc.
Entonces, espero que este os haya servido de algo.
End.

**Referencias:**
* Open Source Power on BPM - A Comparison of JBoss jBPM and Intalio BPMS (By Pin Nie, Riku Seppälä, Måns Hafrén) - <http://jannekorhonen.fi/project_report_final_BPMS.pdf>
* Gestión de Expedientes con Alfresco ECM - <http://holisticsecurity.wordpress.com/2010/10/08/gestion-de-expedientes-con-alfresco-ecm>
* Open Source Workflow Engines in Java - <http://java-source.net/open-source/workflow-engines>
* jBPM5 vs Activiti5? dumb question? - <http://salaboy.wordpress.com/2011/01/19/jbpm5-vs-activiti5-dumb-question>
* Activiti or jBPM, which should I use in my next project? - <http://www.activiti.org/faq.html#ActivitiOrJbpm>
* jBPM Form Builder follow-up - <http://blog.athico.com/2011/07/jbpm-form-builder-follow-up.html>
* Business Process Simulation versus Emulation - <http://onbpms.com/2007/07/18/business-process-simulation-versus-emulation>
* Process Simulation - <http://community.intalio.com/bpms-screencasts/process-simulation.html>
