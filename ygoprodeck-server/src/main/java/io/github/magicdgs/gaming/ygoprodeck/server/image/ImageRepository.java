package io.github.magicdgs.gaming.ygoprodeck.server.image;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ImageRepository extends ListCrudRepository<Image, String> {
	public String formatId(final String type, final String name);

}
