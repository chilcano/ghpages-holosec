# PaaS or micro-PaaS for Microservices? - a simple technology review

_"How do you eat an elephant? One bite at a time"_ - This phrase makes sense, everybody understands It but sometimes is forgotten.
Happily some technology companies have managed to internalize this phrase in its processes and products. 

![PaaS for Microservices](https://www.dropbox.com/s/5alzc0zrk2odg0z/blog-pass-microservices-how%20do%20you%20eat%20an%20elephant-one%20bite%20at%20a%20time.png?raw=1 "PaaS for Microservices")


Below some examples:

- Netflix: Micro Services (http://techblog.netflix.com/2015/02/a-microscope-on-microservices.html)
- Docker: Containers (https://www.docker.com/what-docker)
- Snackson: Micro Learning (http://en.snackson.com/microlearning)
- Twitter: Micro Blogging (https://en.wikipedia.org/wiki/Microblogging)
- Raspberry Pi: Micro Personal Computer (https://en.wikipedia.org/wiki/Raspberry_Pi)

Many people and many companies make big mistake when they are entirely focused on big goals. If you have a big goal, you probably spend a lot of time and effort on achieving it.

Well, I will explain on this blog post How this simple concept is being applied to PaaS (Platform as a Service) today and will mention some opensource!.

## 1. Key concepts

- Agile
  * You aren't avoiding the big goal, you are solving the problem step-by-step. And to do so, you need to be organized, forget obsolete methodologies and not waste time.
  * `Pre-shaved Yaks` (https://www.flickr.com/photos/zimki/243779431/in/photostream).
  * In other words, automates everything you can, organize small teams to create small and independent products, etc.
- K-I-S-S (Keep It simple, stupid)
  * The road is long and difficult (the learning curve is steep), then it makes it easy and enjoyable, and if the stretch is unavoidable, then try to automate it.
- Don't reinvent the wheel
  * Scrum, Kanban, TDD, Unix, Linux, etc. all these are `things that worked before and work now. Please, use them.
- Free as in beer
  * Free and open source

## 2. What is PaaS (Platform as a Service)?

![PaaS definition - Wikipedia](https://www.dropbox.com/s/udg2dgde5fwgavw/blog-paas-microservice-1-paas-definition.png?raw=1 "PaaS definition - Wikipedia")

Wikipedia mentions that `Zimki` was the first `PaaS` and was released in the year 2006. Zimki was an end-to-end JavaScript web application development and utility computing platform that removed all the repetitive tasks encountered when creating web applications and web services. After of Zimki born other:

* Google App Engine
* WSO2 Stratos (Apache Stratos)
* Redhat's OpenShift
* Saleforce's Heroku
* Jelastic
* Etc.

I ask myself, Are they really suitable for creating Microservices today?. In my opinion, Yes, they are suitable but after a heavy lifting and re-designing. 
There are good news about that because the main Software Companies are working on that, making them lighter, more agile and versatile, someone are focused to Cloud, to on-Premise, to Containers or to RAD (rapid application development). Just check out Openshift, CloudFoundry, etc.

## 3. What's out there?

Well, after searching the internet, the result is a first version of the PaaS list. 

### PaaS

1. Zato
  * https://zato.io
  * Open-source ESB, SOA, REST, APIs and cloud integrations in Python.
2. Flynn
  * https://flynn.io
  * Runs anything that can run on Linux, not just stateless web apps. Includes built-in database appliances (just PostgreSQL right now) and handles TCP traffic as well as HTTP and HTTPS.
  * Supports Java, Go, Node.js, PHP, Python and Ruby as languages.
3. Deis
  * http://deis.io
  * Open source PaaS that makes it easy to deploy and manage applications on your own servers. Deis builds upon Docker and CoreOS to provide a lightweight PaaS with a Heroku-inspired workflow.
  * Deis can deploy any language or framework using Docker. If you don't use Docker, Deis also includes Heroku buildpacks for Ruby, Python, Node.js, Java, Clojure, Scala, Play, PHP, Perl, Dart and Go.
4. Tsuru
  * https://tsuru.io
  * https://github.com/tsuru/tsuru
  * Tsuru goes beyond 12 factor apps. Run any application written in any language or framework (Python, Ruby, Go, Java, PHP and it's extensible).
5. Nanobox
  * https://desktop.nanobox.io
  * https://github.com/nanobox-io/nanobox
  * Nanobox allows you to stop configuring environments and just code. It guarantees that any project you start will work the same for anyone else collaborating on the project. When it's time to launch the project, you'll know that your production app will work, because it already works on nanobox.
  * Nanobox detects your app type and automatically configures the environment and installs everything your app needs to run (more of 15 programming languages and frameworks)
4. Otto 
  * https://ottoproject.io
  * Development and Deployment Made Easy (successor to Vagrant).
  * Otto knows how to develop and deploy any application on any cloud platform, all controlled with a single consistent workflow to maximize the productivity of you and your team.
5. Rack
  * https://convox.com
  * https://github.com/convox/rack
  * A Convox Rack is a private Platform-as-a-Service (PaaS). It gives you a place to deploy your web applications and mobile backends without having to worry about managing servers, writing complex deployment recipes, or monitoring process uptime. We call this platform a "Rack".
6. Empire
  * http://engineering.remind.com
  * https://github.com/remind101/empire
  * Empire is a control layer on top of Amazon EC2 Container Service (ECS) that provides a Heroku like workflow. It conforms to a subset of the Heroku Platform API, which means you can use the same tools and processes that you use with Heroku, but with all the power of EC2 and Docker.
7. Dokku
  * http://dokku.viewdocs.io/dokku
  * The smallest PaaS implementation you've ever seen.
  * Powered by Docker, you can install Dokku on any hardware. Use it on inexpensive cloud providers.
  * Dokku by default does not provide any datastores such as MySQL or PostgreSQL. You will need to install plugins to handle that.
8. Gondor
  * https://gondor.io
  * Managed Python hosting with command-line deployment and support for PostgreSQL, Redis, Celery, Elasticsearch and more.
9. AppFog
  * https://www.ctl.io/appfog
  * AppFog, CenturyLink's Platform-as-a-Service (PaaS) based on Cloud Foundry, enables developers to focus on writing great cloud-based applications without having to worry about managing the underlying infrastructure.

### Microservices frameworks

1. Dropwizard
  * http://www.dropwizard.io
  * Dropwizard pulls together stable, mature libraries from the Java ecosystem into a simple, light-weight package that lets you focus on getting things done.
  * Dropwizard has out-of-the-box support for sophisticated configuration, application metrics, logging, operational tools, and much more, allowing you and your team to ship a production-quality web service in the shortest time possible.
2. Ratpack
  * https://ratpack.io
  * Ratpack is a set of Java libraries for building modern HTTP applications. It provides just enough for writing practical, high performance, apps. It is built on Java 8, Netty and reactive principles.
3. Spark
  * http://sparkjava.com
  * Spark - A micro framework for creating web applications in Java 8 with minimal effort
4. Vertx
  * http://vertx.io
  * Vert.x is event driven and non blocking. This means your app can handle a lot of concurrency using a small number of kernel threads. Vert.x lets your app scale with minimal hardware.
  * You can use Vert.x with multiple languages including Java, JavaScript, Groovy, Ruby, and Ceylon.
5. Seneca
  * http://senecajs.org
  * Seneca is a microservices toolkit for Node.js. It helps you write clean, organized code that you can scale and deploy at any time.
6. Kong
  * https://getkong.org
  * Kong is a scalable, open source API Layer (also known as an API Gateway, or API Middleware). Kong runs in front of any RESTful API and is extended through Plugins, which provide extra functionalities and services beyond the core platform.
  * Kong is built on top of reliable technologies like NGINX and Apache Cassandra, and provides you with an easy to use RESTful API to operate and configure the system.
7. Unirest
  * http://unirest.io
  * Unirest is a set of lightweight HTTP libraries available in multiple languages (Node.js, Ruby, PHP, Java, Python, Objective-C, .Net).

## 4. Conclusions

* As you can see, the trend is to provide a set of tools to do more easy the application development on-premise or/and on-cloud. The idea behind is to remove all the repetitive tasks encountered when creating web applications and web services (aspects related to infrastructure and operations from setting up servers, scaling, configuration, security and backups), the `Pre-Shaved Yaks` concept was introduced. 
* In other side, there are custom PaaS created from existing lightweight frameworks using Docker Containers, below some references. This confirms, right now, that there are mature tools and frameworks ready to be used in the construction of these platforms.

## 5. References

- Introducing Empire: A self-hosted PaaS built on Docker & Amazon ECS
  * By Eric Holmes
  * June 16, 2015
  * http://engineering.remind.com/introducing-empire
- Making a PaaS
  * By Danaelle David
  * June, 2016
  * https://www.cloudreach.com/gb-en/2015/06/making-paas
- What makes a good side project (PaaS)?
  * By Andrew Tork Baker
  * March 18, 2014
  * http://andrewtorkbaker.com/what-makes-a-good-side-project
- Flynn vs. Deis: The Tale of Two Docker Micro-PaaS Technologies.
  * By Lucas Perkins
  * May 27, 2014
  * https://www.ctl.io/developers/blog/post/flynn-vs-deis-the-tale-of-two-docker-micro-paas-technologies
- Stack catalogs 
  * http://www.fullstackpython.com
  * http://www.paasify.it
