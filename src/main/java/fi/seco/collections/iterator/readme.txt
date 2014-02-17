Responsible person: Eetu

Various iterator implementations, whose names hopefully tell what they 
do. These iterators, particularly the filtering and mapping iterators 
are used extensively in the architecture to support "lazy computation", 
triggering calculations only when they are really needed at the final 
UI level.

If you are actually really generating stuff, to go along with the rest 
of the architecture you probably want to return a subclass of 
ALazyGeneratingIterator, and implement it so that a new answer is 
calculated only on each generateNext() call (returning null means no more
results)

AMappingIterator/MappingIterator and AFilteringIterator/FilteringIterator 
are alternatives. With the abstract classes, you have to inline subclass 
them and implement the map/accept -methods, with the concrete classes 
you'll probably inline instantiate a subclass of IMapper/IFilter. You 
probably want to use the abstract classes in most cases.