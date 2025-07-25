# Project Radar

[![CI](https://github.com/jacq42/project-radar/actions/workflows/main.yml/badge.svg)](https://github.com/jacq42/project-radar/actions/workflows/main.yml)
[![PITest](https://github.com/jacq42/project-radar/actions/workflows/testQuality.yml/badge.svg)](https://github.com/jacq42/project-radar/actions/workflows/testQuality.yml)

## TODOs

- [x] Add profile as json or markdown file
- [x] Add project reader for markdown or pdf files
- [x] Add APIs/Scraper for platforms (freelance.de. freelancermap.de, etc.)
- [x] Add embedding and matching engine
- [x] List relevant projects: first sort by similarity, then by relevance
- [x] Add REST endpoint to get matches
- [ ] Add frontend to display projects in user-friendly way

## Tech stack (WIP)

- Backend: [Spring AI](https://docs.spring.io/spring-ai/reference/index.html)
- Frontend: [React](https://react.dev/)
- AI: 
  - [LangChain4j](https://docs.langchain4j.dev/) ???
  - Embedding Model: text-embedding-3-small
  - chat-model: gpt-4o-mini
- Scraping:
  - [Playwright](https://playwright.dev/)


