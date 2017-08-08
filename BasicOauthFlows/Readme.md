http://developer.okta.com/docs/api/resources/oauth2.html

Basics Flows  
============

The OAuth 2.0 APIs each have several different query params which dictate which type of flow you are using and the mechanics of that flow.

At the very basic level, the main API endpoints are:

Authorize endpoint initiates an OAuth 2.0 request.

Token endpoint redeems an authorization grant (returned by the Authorize endpoint) for an access token.



Diff Types of Application 
==========================

1)Browser/Single-Page Application -Uses Implicit Flow

2)Native Application - Uses Authorization Code Grant Flow

 e.g Can use custom redirect URIs like myApp://oauth:2.0:native

3)Web Application - Uses Authorization Code Grant Flow
