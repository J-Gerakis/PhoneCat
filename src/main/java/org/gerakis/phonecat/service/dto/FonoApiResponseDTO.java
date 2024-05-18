package org.gerakis.phonecat.service.dto;

public record FonoApiResponseDTO(
        String technology,
        String _2gs_bands,
        String _3gs_bands,
        String _4gs_bands)
{ }
