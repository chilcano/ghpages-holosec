---
layout:     post
title:      "DevOps is to SDLC as MLOps is to Machine Learning Applications"
#date:       2020-02-20 10:00:00 +0100
categories: ['bigdata', 'devops', 'sdlc','data science'] 
tags:       ['kubernetes', 'MLOps', 'CI/CD']
permalink:  "/2020/03/09/devops-is-to-sdlc-as-mlops-is-to-machine-learning-apps"
---
If you have read the previous post about [Security along the Container-based SDLC](https://holisticsecurity.io/2020/02/10/security-along-the-container-based-sdlc), then you have noted that DevOps and Security practices should be applied and embeded along [SDLC](https://en.wikipedia.org/wiki/Systems_development_life_cycle). Before we had to understand the entire software production process and sub-processes in order to apply these DevOps and Security practices. Well, in this post I'll explain how to apply DevOps practices along Machine Learning Software Applications Development Life Cycle (ML-SDLC) and I'll share a set of tools focusing to implement MLOps.

[![Security along the Container-based SDLC - Overview](/assets/img/20200210-security-along-container-based-sdlc-v1.png "Security along the Container-based SDLC - Overview")](/assets/img/20200210-security-along-container-based-sdlc-v1.png){:target="_blank"}

## Data Science (& ML) projects challenges 

The only thing in common between the Data Science Projects and Software Projects is that both follow more or less the SDLC, that means that challenges and problems that Data Science Projects have to face also are the same.   
Cloud Native Applications following SDLC are trying to be more Agile adopting DevOps practices, and that is the same that Data Science Projects need to do and are doing it. 

1. Data Lifecycle Management
2. Model Lifecycle Management
3. Inferencing
4. Automation everything and anywhere
5. Observability (Monitoring and Metrics)

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



