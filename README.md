hospitalfinder
==============

A Hospital Finder Application developed as part of the mini-project at the 10th Semantic Web Summer School (in under 3 days)
The application relies on a previously generated JSON Data Structure listing all the hospitals in the United States which performs treatments on Cardiac Arrhythmia, by executing a SPARQL Query against a repository stroed under the Local Sesame Store. This data has been aggregated from various public datasets like Medicare Health Compare, CMS.gov Inpatient Datasets, etc. and semantically annotated using a simplistic data model, using Google Refine.
The visualization is created using Exhibit 2.0 and Google Maps - Snippets of the Code have been derived from the examples on the Exhibit website.
The gist of the Java code is https://gist.github.com/maulikkamdar/5999514
The future work includes merging the offline query execution to make it possible for dynamic search and structuring the application to something actually useful.
