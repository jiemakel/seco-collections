Responsible person: Eetu

Various collection implementations, whose names hopefully tell what they do. Used less than
the corresponding iterators.

AMappingCollection/MappingCollection and AFilteringCollection/FilteringCollection are
alternatives. With the abstract classes, you have to inline subclass them and implement the 
map/accept -methods, with the concrete classes you'll probably inline instantiate a 
subclass of IMapper/IFilter. You probably want to use the abstract classes in most cases.