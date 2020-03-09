---
layout:     post
title:      "DevOps is to SDLC as MLOps is to Machine Learning Applications"
#date:       2020-02-20 10:00:00 +0100
categories: ['bigdata', 'devops', 'sdlc','data science', 'artificial intelligence'] 
tags:       ['kubernetes', 'MLOps', 'CI/CD','machine learning']
permalink:  "/2020/03/09/devops-is-to-sdlc-as-mlops-is-to-machine-learning-apps"
---
If you have read the previous post about [Security along the Container-based SDLC](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc), then you have noted that DevOps and Security practices should be applied and embeded along [SDLC](https://en.wikipedia.org/wiki/Systems_development_life_cycle). Before we had to understand the entire software production process and sub-processes in order to apply these DevOps and Security practices. Well, in this post I'll explain how to apply DevOps practices along Machine Learning Software Applications Development Life Cycle (ML-SDLC) and I'll share a set of tools focusing to implement MLOps.

[![](/assets/blog20200309/mlops-sdlc-devsecops-comparison.png)](/assets/blog20200309/mlops-sdlc-devsecops-comparison.png){:target="_blank"}


## Concepts and definitions

**Artificial Intelligence**
> Computer science defines AI research as the study of "intelligent agents": any device that perceives its environment and takes actions that maximize its chance of successfully achieving its goals. A more elaborate definition characterizes AI as "a system's ability to correctly interpret external data, to learn from such data, and to use those learnings to achieve specific goals and tasks through flexible adaptation."  
> https://en.wikipedia.org/wiki/Artificial_intelligence

**Machine Learning**
> Machine learning (ML) is the scientific study of algorithms and statistical models that computer systems use to perform a specific task without using explicit instructions, relying on patterns and inference instead.
> https://en.wikipedia.org/wiki/Machine_learning 

**Inference**
> Inferences are steps in reasoning, moving from premises to logical consequences; etymologically, the word infer means to "carry forward". Inference is theoretically traditionally divided into deduction and induction.
> https://en.wikipedia.org/wiki/Inference

**Data Science**
> Data science is an inter-disciplinary field that uses scientific methods, processes, algorithms and systems to extract knowledge and insights from many structural and unstructured data. Data science is related to data mining and big data.  
> Data science is a "concept to unify statistics, data analysis, machine learning and their related methods" in order to "understand and analyze actual phenomena" with data.  
> https://en.wikipedia.org/wiki/Data_science


## Data Science (& ML) challenges 

Undoubtedly this era belongs to Artificial Intelligence (AI), and this results in the use of Machine Learning in almost every field, trying to solve different kind of problems from healthcare, in business fields, and technical spaces, Machine Learning is everywhere. That, the Open Source Software (OSS) and Cloud-based Distributed Computing have caused the appearance of many tools, techniques, and algorithms and the ___development of Machine Learning models to solve a problem is not a challenge, the real challenge lies in the management of these models and their data at a massive scale___. 

The Data Science (& ML) Development Process needs to learn from SDLC (Software Engineering) in order to face these challenges, and What are these challenges?. The answer is: They are the same challenges that SDLC (Software Engineering) is facing by adopting the DevOps Practices, for example:

1. Data challenges
   * Dataset dependencies: data in training and in evaluation stages can vary in real-world scenarios.
2. Model challenges
   * ML models are built in a Data scientist sandbox. It was not developed to take scalability in mind; rather, it was just developed to get good accuracies and right algorithm. 
3. Automation everything and anywhere
   * Training a simple model and putting it into inference and generating prediction is a simple and manual. In real-world cases (at scale) that must be automated. 
4. Observability (Monitoring and Metrics)

![](/assets/blog20200309/PGS-Software-MLOps-2.png){:width="400"}  
> More Effective Machine Learning Production with MLOps, December 11, 2019 Maciej Mazur   
> https://www.pgs-soft.com/blog/more-effective-machine-learning-production-with-mlops  

## Data Science (& ML) Life Cycle




## What is MLOps?

> MLOps (a compound of “machine learning” and “operations”) is a practice for collaboration and communication between data scientists and operations professionals to help manage production ML (or deep learning) lifecycle.[1] Similar to the DevOps or DataOps approaches, MLOps looks to increase automation and improve the quality of production ML while also focusing on business and regulatory requirements. While MLOps also started as a set of best practices, it is slowly evolving into an independent approach to ML lifecycle management. MLOps applies to the entire lifecycle - from integrating with model generation (software development lifecycle, continuous integration/continuous delivery), orchestration, and deployment, to health, diagnostics, governance, and business metrics. 
> https://en.wikipedia.org/wiki/MLOps


## Tools

1. https://airflow.apache.org
2. https://www.kubeflow.org
3. 



## References

MLOps on Kubernetes with Portable Profiles
December 05, 2019 
https://www.weave.works/blog/mlops-on-kubernetes-with-portable-profiles


What would machine learning look like if you mixed in DevOps? Wonder no more, we lift the lid on MLOps
 By Ryan Dawson 7 Mar 2020 at 10:00
https://www.theregister.co.uk/2020/03/07/devops_machine_learning_mlops/


What is a Pipeline in Machine Learning? How to create one?
By Shashanka M, Dec 10, 2019
https://medium.com/analytics-vidhya/what-is-a-pipeline-in-machine-learning-how-to-create-one-bda91d0ceaca


MLOps: CI/CD for Machine Learning Pipelines & Model Deployment with Kubeflow
Published on: October 25, 2019 
https://growingdata.com.au/mlops-ci-cd-for-machine-learning-pipelines-model-deployment-with-kubeflow/