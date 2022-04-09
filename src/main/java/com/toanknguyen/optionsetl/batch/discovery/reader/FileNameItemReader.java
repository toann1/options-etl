package com.toanknguyen.optionsetl.batch.discovery.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileNameItemReader implements ResourceAwareItemReaderItemStream<String> {
    private Resource resource;
    private boolean isRead;

    public FileNameItemReader() {
        this.isRead = false;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String read() {
        if (this.isRead) {
            // reset flag for next resource
            this.isRead = false;
            return null;
        }
        // instruct spring batch to move on to the next file
        this.isRead = true;
        return resource.getFilename();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        // no implementation as this class only reads the resource's file name
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // no implementation as this class only reads the resource's file name
    }

    @Override
    public void close() {
        // no implementation as this class only reads the resource's file name
    }
}
