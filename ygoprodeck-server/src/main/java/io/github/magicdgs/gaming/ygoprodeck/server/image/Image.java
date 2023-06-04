package io.github.magicdgs.gaming.ygoprodeck.server.image;

import org.springframework.core.io.Resource;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
@SuperBuilder
public class Image {

	@Id
	private String id;

	private String name;
	private String type;
	
	private Resource content;
}
