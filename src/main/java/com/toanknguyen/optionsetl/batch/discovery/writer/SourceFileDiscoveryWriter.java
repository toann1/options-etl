package com.toanknguyen.optionsetl.batch.discovery.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SourceFileDiscoveryWriter implements ItemWriter<String> {
    public static final Logger logger = LoggerFactory.getLogger(SourceFileDiscoveryWriter.class);

    @Override
    public void write(List<? extends String> items) {
        // TODO
        logger.info("writing: {}", items);
    }
}
