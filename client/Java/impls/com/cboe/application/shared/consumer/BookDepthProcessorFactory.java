package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.BookDepthCollector;


public class BookDepthProcessorFactory {

  public BookDepthProcessorFactory() {
    super();
  }

  public static BookDepthProcessor create(BookDepthCollector parent) {
    BookDepthProcessor processor = new BookDepthProcessor(parent);
    return processor;
  }
}
