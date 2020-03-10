---
layout:     post
title:      "DevOps is to SDLC as MLOps is to Machine Learning Applications"
date:       2020-03-10 10:00:00 +0100
categories: ['Bigdata', 'DevOps', 'SDLC','Data Science', 'AI', 'ML'] 
tags:       ['Kubernetes', 'MLOps', 'CI/CD']
permalink:  "/2020/03/10/devops-is-to-sdlc-as-mlops-is-to-machine-learning-apps"
---
If you have read the previous post about [Security along the Container-based SDLC](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc), then you have noted that DevOps and Security practices should be applied and embeded along [SDLC](https://en.wikipedia.org/wiki/Systems_development_life_cycle). Before we had to understand the entire software production process and sub-processes in order to apply these DevOps and Security practices. Well, in this post I'll explain how to apply DevOps practices along Machine Learning Software Applications Development Life Cycle (ML-SDLC) and I'll share a set of tools focusing to implement MLOps.

[![](/assets/blog20200309/mlops-sdlc-devsecops-comparison.png)](/assets/blog20200309/mlops-sdlc-devsecops-comparison.png){:target="_blank"}  
_<center>Data Science (& ML) Life Cycle</center>_


## Concepts and definitions

**Artificial Intelligence**
> Computer science defines AI research as the study of "intelligent agents": any device that perceives its environment and takes actions that maximize its chance of successfully achieving its goals. A more elaborate definition characterizes AI as "a system's ability to correctly interpret external data, to learn from such data, and to use those learnings to achieve specific goals and tasks through flexible adaptation."  
> [https://en.wikipedia.org/wiki/Artificial_intelligence](https://en.wikipedia.org/wiki/Artificial_intelligence)

**Machine Learning**
> Machine learning (ML) is the scientific study of algorithms and statistical models that computer systems use to perform a specific task without using explicit instructions, relying on patterns and inference instead.
> [https://en.wikipedia.org/wiki/Machine_learning](https://en.wikipedia.org/wiki/Machine_learning)

**Inference**
> Inferences are steps in reasoning, moving from premises to logical consequences; etymologically, the word infer means to "carry forward". Inference is theoretically traditionally divided into deduction and induction.
> [https://en.wikipedia.org/wiki/Inference](https://en.wikipedia.org/wiki/Inference)

**Data Science**
> Data science is an inter-disciplinary field that uses scientific methods, processes, algorithms and systems to extract knowledge and insights from many structural and unstructured data. Data science is related to data mining and big data.  
> Data science is a "concept to unify statistics, data analysis, machine learning and their related methods" in order to "understand and analyze actual phenomena" with data.  
> [https://en.wikipedia.org/wiki/Data_science](https://en.wikipedia.org/wiki/Data_science)


## Data Science (& ML) challenges 

Undoubtedly this era belongs to Artificial Intelligence (AI), and this results in the use of Machine Learning in almost every field, trying to solve different kind of problems from healthcare, in business fields, and technical spaces, Machine Learning is everywhere. That, the Open Source Software (OSS) and Cloud-based Distributed Computing have caused the appearance of many tools, techniques, and algorithms and the ___development of Machine Learning models to solve a problem is not a challenge, the real challenge lies in the management of these models and their data at a massive scale___. 

[![](/assets/blog20200309/PGS-Software-MLOps-2.png){:width="800"}](/assets/blog20200309/PGS-Software-MLOps-2.png) | More Effective Machine Learning Production with MLOps, December 11, 2019 Maciej Mazur (https://www.pgs-soft.com/blog/more-effective-machine-learning-production-with-mlops)  

The Data Science (& ML) Development Process needs to learn from SDLC (Software Engineering) in order to face these challenges, and What are these challenges?. The answer is: They are the same challenges that SDLC (Software Engineering) is facing by adopting the DevOps Practices, for example:

**1. Data challenges**  
Dataset dependencies. Data in training and in evaluation stages can vary in real-world scenarios.
  
**2. Model challenges**  
ML models are built in a Data scientist sandbox. It was not developed to take scalability in mind; rather, it was just developed to get good accuracies and right algorithm.   
Scale ML Applications.  

**3. Automation**  
Training a simple model and putting it into inference and generating prediction is a simple and manual task. In real-world cases (at scale) that must be automated everything and anywhere.   
Automation is the only way to achieve scalability in the different stages of ML-SDLC.

**4. Observability**  
Monitoring, Alerting, Visualization and Metrics.  

**5. The MLOps Tool Scene**  
The effort involved in solving MLOps challenges can be reduced by leveraging a platform and applying it to the particular case.  
The AI (& ML) tool landscape is complex with different tools specialising in different niches and in some cases there are competing tools approaching similar problems in different ways (see the below Linux Foundation's AI project for a categorised lists of tools).

[![](/assets/blog20200309/202003109-linux-foundation-ai-landscape.png)](/assets/blog20200309/202003109-linux-foundation-ai-landscape.png){:target="_blank"}  
_<center>The Linux Foundation's AI (& ML) tool landscape - 2020/03/09</center>_


## What is MLOps?

> MLOps (a compound of “machine learning” and “operations”) is a practice for collaboration and communication between data scientists and operations professionals to help manage production ML (or deep learning) lifecycle.[1] Similar to the DevOps or DataOps approaches, MLOps looks to increase automation and improve the quality of production ML while also focusing on business and regulatory requirements. While MLOps also started as a set of best practices, it is slowly evolving into an independent approach to ML lifecycle management. MLOps applies to the entire lifecycle - from integrating with model generation (software development lifecycle, continuous integration/continuous delivery), orchestration, and deployment, to health, diagnostics, governance, and business metrics. 
> [https://en.wikipedia.org/wiki/MLOps](https://en.wikipedia.org/wiki/MLOps)


## Selected tools to support MLOps and criteria used

If you have reviewed above [The Linux Foundation’s AI project landscape (categorised lists of tools)](https://landscape.lfai.foundation/images/landscape.png), you have realized that there a plenty of different tools, commercial and opensource. Well, the next list of products is a subset of products that follow this criteria:

**1. Open Source**  
Perfect for early adopters, also suitable to implement easily Proof-of-concepts or starting your own personal project.

**2. Kubernetes-based**  
Kubernetes and Containers are the new platform where our Applications are going to run and live, even the ML Applications.

**3. Platform**  
I don’t want to waste efforts integrating heterogeneous tools, I want a stack or platform with mature tools already integrated seamlessly.


[](){:name="oss-doc-link"}

<iframe src="https://docs.google.com/spreadsheets/d/e/2PACX-1vTXcNBZjmHMfFHGmwizy_olJ0WP3aOA_lax4lRe9Y1DjG7XARstwm53ovFaovkirt7ybeyq8ybr9Tck/pubhtml?widget=true&amp;headers=false" width="800" height="800"></iframe>
_<center>Open Source Tools (Platforms) to support the MLOps</center>_

## References


* [What would machine learning look like if you mixed in DevOps? Wonder no more, we lift the lid on MLOps - By Ryan Dawson, 7 Mar 2020](https://www.theregister.co.uk/2020/03/07/devops_machine_learning_mlops)
* [MLOps Platform: Productionizing Machine Learning Models - By Navdeep Singh Gill, 2 Sep 2019](https://www.xenonstack.com/blog/mlops)
* [ML Ops: Machine Learning as an Engineering Discipline - By Cristiano Breuel, 3 Jan 2020](https://towardsdatascience.com/ml-ops-machine-learning-as-an-engineering-discipline-b86ca4874a3f)
* [MLOps: CI/CD for Machine Learning Pipelines & Model Deployment with Kubeflow - 25 Oct 2019](https://growingdata.com.au/mlops-ci-cd-for-machine-learning-pipelines-model-deployment-with-kubeflow)
* [The Linux Foundation’s AI project landscape (categorised lists of tools)](https://landscape.lfai.foundation/images/landscape.png)
* [The Institute for Ethical AI & Machine Learning - Awesome production machine learning](https://github.com/EthicalML/awesome-production-machine-learning)